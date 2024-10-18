package src.pas.chess.heuristics;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.Game;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.utils.Coordinate;

import java.util.HashMap;
import java.util.Map;

// JAVA PROJECT IMPORTS
import src.pas.chess.heuristics.DefaultHeuristics;


public class CustomHeuristics
    extends Object
{

	// Heuristic for material balance: how many pieces each player has and their value
    private static double getMaterialBalance(DFSTreeNode node) {
        double score = 0.0;

        // Max player's material
        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node))) {
            score += Piece.getPointValue(piece.getType());
        }

        // Min player's material (subtracted)
        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMinPlayer(node))) {
            score -= Piece.getPointValue(piece.getType());
        }

        return score;
    }

    // Heuristic for evaluating king safety
    private static double evaluateKingSafety(DFSTreeNode node) {
        double score = 0.0;

        // Max player's king safety
        score += evaluateKingSafetyForPlayer(node, DefaultHeuristics.getMaxPlayer(node));

        // Min player's king safety (negatively weighted)
        score -= evaluateKingSafetyForPlayer(node, DefaultHeuristics.getMinPlayer(node));

        return score;
    }

    // Helper method to evaluate a single player's king safety
    private static double evaluateKingSafetyForPlayer(DFSTreeNode node, Player player) {
        double score = 0.0;
        Game game = node.getGame();
        Piece king = game.getBoard().getPieces(player, PieceType.KING).iterator().next();
        Coordinate kingPos = game.getCurrentPosition(king);

        int friendlyPiecesNearby = 0;
        int enemyPiecesAttacking = 0;

        // Check friendly pieces near the king
        for (Piece piece : game.getBoard().getPieces(player)) {
            if (piece.getType() != PieceType.KING) {
                Coordinate pos = game.getCurrentPosition(piece);
                if (Math.abs(kingPos.getXPosition() - pos.getXPosition()) <= 1 && 
                    Math.abs(kingPos.getYPosition() - pos.getYPosition()) <= 1) {
                    friendlyPiecesNearby++;
                }
            }
        }

        // Check enemy pieces attacking the king
        for (Piece enemyPiece : game.getBoard().getPieces(game.getOtherPlayer(player))) {
            if (enemyPiece.getAllCaptureMoves(game).contains(kingPos)) {
                enemyPiecesAttacking++;
            }
        }

        // Reward friendly pieces near the king
        score += friendlyPiecesNearby * 1.0;

        // Penalize enemy pieces attacking the king
        score -= enemyPiecesAttacking * 3.0;

        return score;
    }

    // Heuristic for evaluating piece mobility: how many moves each player can make
    private static double evaluatePieceMobility(DFSTreeNode node) {
        double score = 0.0;

        // Max player's mobility
        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node))) {
            int numMoves = piece.getAllMoves(node.getGame()).size();
            score += numMoves * 0.1;
        }

        // Min player's mobility (negatively weighted)
        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMinPlayer(node))) {
            int numMoves = piece.getAllMoves(node.getGame()).size();
            score -= numMoves * 0.1;
        }

        return score;
    }

    // Heuristic for center control: control over the central squares
    private static double evaluateCenterControl(DFSTreeNode node) {
        double score = 0.0;
        Coordinate[] centerSquares = {
            new Coordinate(3, 3), new Coordinate(3, 4),
            new Coordinate(4, 3), new Coordinate(4, 4)
        };

        for (Coordinate center : centerSquares) {
            Piece piece = node.getGame().getBoard().getPieceAtPosition(center);
            if (piece != null) {
                if (piece.getPlayer().equals(DefaultHeuristics.getMaxPlayer(node))) {
                    score += 0.5;
                } else {
                    score -= 0.5;
                }
            }
        }

        return score;
    }

    // Heuristic for pawn structure: evaluates doubled, isolated, and passed pawns
    private static double evaluatePawnStructure(DFSTreeNode node) {
        double score = 0.0;

        // Evaluate max player's pawn structure
        score += evaluatePawnFeatures(node, DefaultHeuristics.getMaxPlayer(node));

        // Evaluate min player's pawn structure (negatively weighted)
        score -= evaluatePawnFeatures(node, DefaultHeuristics.getMinPlayer(node));

        return score;
    }

    // Helper method for evaluating pawn structure of a single player
    private static double evaluatePawnFeatures(DFSTreeNode node, Player player) {
        double score = 0.0;
        Game game = node.getGame();
        Map<Integer, Integer> pawnFiles = new HashMap<>();

        // Count pawns on each file (for doubled and isolated pawns)
        for (Piece pawn : game.getBoard().getPieces(player, PieceType.PAWN)) {
            Coordinate pos = game.getCurrentPosition(pawn);
            int file = pos.getXPosition();
            pawnFiles.put(file, pawnFiles.getOrDefault(file, 0) + 1);
        }

        // Penalize doubled pawns
        for (int count : pawnFiles.values()) {
            if (count > 1) {
                score -= (count - 1) * 0.5; // Penalize doubled pawns
            }
        }

        return score;
    }

    /**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
	 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
	 * in DefaultHeuristics.java (which is in the same directory as this file)
	 */
    public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
		// please replace this!
     
        double materialBalance = getMaterialBalance(node);
        double kingSafety = evaluateKingSafety(node);
        double pieceMobility = evaluatePieceMobility(node);
        double centerControl = evaluateCenterControl(node);
        double pawnStructure = evaluatePawnStructure(node);

        // Combine the heuristic scores with appropriate weights
        return materialBalance * 15.0 + kingSafety * 10.0 + pieceMobility * 0.5 + centerControl * 1.5 + pawnStructure * 0.5;
    }

}
