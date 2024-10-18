package src.pas.chess.moveorder;


// SYSTEM IMPORTS
import edu.bu.chess.search.DFSTreeNode;

import java.util.List;


// JAVA PROJECT IMPORTS
import src.pas.chess.moveorder.DefaultMoveOrderer;

public class CustomMoveOrderer
    extends Object
{

	/**
	 * TODO: implement me!
	 * This method should perform move ordering. Remember, move ordering is how alpha-beta pruning gets part of its power from.
	 * You want to see nodes which are beneficial FIRST so you can prune as much as possible during the search (i.e. be faster)
	 * @param nodes. The nodes to order (these are children of a DFSTreeNode) that we are about to consider in the search.
	 * @return The ordered nodes.
	 */
	public static List<DFSTreeNode> order(List<DFSTreeNode> nodes) {
        // please replace this!
        List<DFSTreeNode> captureNodes = new LinkedList<>();
        List<DFSTreeNode> promotionNodes = new LinkedList<>();
        List<DFSTreeNode> castlingNodes = new LinkedList<>();
        List<DFSTreeNode> enPassantNodes = new LinkedList<>();
        List<DFSTreeNode> checkNodes = new LinkedList<>();
        List<DFSTreeNode> otherNodes = new LinkedList<>();

        for (DFSTreeNode node : nodes) {
            if (node.getMove() != null) {
                switch (node.getMove().getType()) {
                    case CAPTUREMOVE:
                        captureNodes.add(node);
                        break;
                    case PROMOTEPAWNMOVE:
                        promotionNodes.add(node);
                        break;
                    case CASTLEMOVE:
                        castlingNodes.add(node);
                        break;
                    case ENPASSANTMOVE:
                        enPassantNodes.add(node);
                        break;
                    default:
                        if (isCheckMove(node)) {
                            checkNodes.add(node);
                        } else {
                            otherNodes.add(node);
                        }
                        break;
                }
            } else {
                otherNodes.add(node);
            }
        }

        captureNodes.addAll(promotionNodes);
        captureNodes.addAll(enPassantNodes);
        captureNodes.addAll(castlingNodes);
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
