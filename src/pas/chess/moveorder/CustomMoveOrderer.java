package src.pas.chess.moveorder;

import edu.bu.chess.search.DFSTreeNode;
import edu.bu.chess.game.Game;
import edu.bu.chess.game.piece.Piece;
import edu.bu.chess.game.piece.PieceType;
import edu.bu.chess.game.player.Player;
import edu.bu.chess.utils.Coordinate;

import java.util.LinkedList;
import java.util.List;

// JAVA PROJECT IMPORTS
import src.pas.chess.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer extends Object {

    /**
     * This method orders the moves by prioritizing beneficial ones such as captures, checks, and promotions.
     * @param nodes The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
     * @return The ordered nodes.
     */
    public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {   
        List<DFSTreeNode> captureNodes = new LinkedList<>();
        List<DFSTreeNode> promotionNodes = new LinkedList<>();
        List<DFSTreeNode> checkNodes = new LinkedList<>();
        List<DFSTreeNode> otherNodes = new LinkedList<>();

        for (DFSTreeNode node : nodes) {
            if (node.getMove() != null) {
                switch (node.getMove().getType()) {
                    case CAPTUREMOVE:
                        captureNodes.add(node);  // prioritize captures
                        break;
                    case PROMOTEPAWNMOVE:
                        promotionNodes.add(node);  // prioritize promotions
                        break;
                    default:
                        if (isCheckMove(node)) {
                            checkNodes.add(node);  // prioritize moves that check the king
                        } else {
                            otherNodes.add(node);  // add the rest
                        }
                        break;
                }
            } else {
                otherNodes.add(node);  // moves with no type
            }
        }

        // Combine the prioritized moves
        captureNodes.addAll(promotionNodes);
        captureNodes.addAll(checkNodes);
        captureNodes.addAll(otherNodes);

        return captureNodes;
    }

    /**
     * Determines if a move puts the opponent's king in check.
     * @param node The current game state node after the move.
     * @return True if the move puts the opponent's king in check, false otherwise.
     */
    private static boolean isCheckMove(DFSTreeNode node) {
        Game game = node.getGame();
        Player opponent = game.getOtherPlayer(node.getMaxPlayer());

        for (Piece piece : game.getBoard().getPieces(opponent, PieceType.KING)) {
            Coordinate kingPosition = game.getCurrentPosition(piece);
            for (Piece maxPlayerPiece : game.getBoard().getPieces(node.getMaxPlayer())) {
                if (maxPlayerPiece.getAllCaptureMoves(game).contains(kingPosition)) {
                    return true;
                }
            }
        }
        return false;
    }
}
