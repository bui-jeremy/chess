package src.pas.chess.heuristics;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;


// JAVA PROJECT IMPORTS
import src.pas.chess.heuristics.DefaultHeuristics;


public class CustomHeuristics
    extends Object
{

	public static double getOffensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        double score = 0.0;

        int numThreatenedPieces = DefaultHeuristics.OffensiveHeuristics.getNumberOfPiecesMaxPlayerIsThreatening(node);

        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMinPlayer(node))) {
            if (isPieceThreatened(node, piece)) { 
                score += Piece.getPointValue(piece.getType());
            }
        }

        score += countPawnsCloseToPromotion(node);

        return score + numThreatenedPieces;
    }

    public static double getDefensiveMaxPlayerHeuristicValue(DFSTreeNode node) {
        double score = 0.0;

        score += DefaultHeuristics.DefensiveHeuristics.getNumberOfMaxPlayersAlivePieces(node);

        score -= DefaultHeuristics.DefensiveHeuristics.getNumberOfPiecesThreateningMaxPlayer(node);

        score += DefaultHeuristics.DefensiveHeuristics.getClampedPieceValueTotalSurroundingMaxPlayersKing(node);

        return score;
    }

    public static double getNonlinearPieceCombinationMaxPlayerHeuristicValue(DFSTreeNode node) {
        double score = 0.0;

        score += DefaultHeuristics.getNonlinearPieceCombinationMaxPlayerHeuristicValue(node);

        score += countDevelopedPieces(node);

        return score;
    }

    private static boolean isPieceThreatened(DFSTreeNode node, Piece piece) {
        for (Piece enemyPiece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMinPlayer(node))) {
            if (enemyPiece.getAllCaptureMoves(node.getGame()).contains(node.getGame().getCurrentPosition(piece))) {
                return true;
            }
        }
        return false;
    }

    private static double countPawnsCloseToPromotion(DFSTreeNode node) {
        double score = 0.0;

        for (Piece pawn : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node), PieceType.PAWN)) {
            Coordinate position = node.getGame().getCurrentPosition(pawn);
            if (isCloseToPromotion(position)) {
                score += 10; 
            }
        }

        return score;
    }


    private static boolean isCloseToPromotion(Coordinate position) {
        return position.getYPosition() == 7; 
    }

    private static double countDevelopedPieces(DFSTreeNode node) {
        double score = 0.0;

        for (Piece piece : node.getGame().getBoard().getPieces(DefaultHeuristics.getMaxPlayer(node))) {
            if (!isInStartingPosition(piece, node)) {
                score += 1.0; 
            }
        }

        return score;
    }


    private static boolean isInStartingPosition(Piece piece, DFSTreeNode node) {
        Coordinate position = node.getGame().getCurrentPosition(piece);
        PieceType type = piece.getType();
        Player player = DefaultHeuristics.getMaxPlayer(node);

        if (type == PieceType.PAWN) {
            int startingRank = player.getPlayerID() == 0 ? 1 : 6; 
            return position.getYPosition() == startingRank;
        }

        return false; 
    }

    /**
	 * TODO: implement me! The heuristics that I wrote are useful, but not very good for a good chessbot.
	 * Please use this class to add your heuristics here! I recommend taking a look at the ones I provided for you
	 * in DefaultHeuristics.java (which is in the same directory as this file)
	 */
    public static double getMaxPlayerHeuristicValue(DFSTreeNode node) {
		// please replace this!
        double offenseHeuristic = getOffensiveMaxPlayerHeuristicValue(node);
        double defenseHeuristic = getDefensiveMaxPlayerHeuristicValue(node);
        double nonlinearHeuristic = getNonlinearPieceCombinationMaxPlayerHeuristicValue(node);

        // Combine all heuristics into a single value
        return offenseHeuristic + defenseHeuristic + nonlinearHeuristic;
    }

}
