package utils;

import model.Cell;
import model.GameBoard;
import model.Position;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static List<Cell> getReachablePointsInFourDirections(GameBoard gameBoard, Position posA) {
        List<Cell> res = new ArrayList<>();
        for (int i = posA.getRow() + 1; i < gameBoard.getRowCnt(); i++) {
            if (gameBoard.getCell(i, posA.getCol()).isEmpty()) {
                res.add(gameBoard.getCell(i, posA.getCol()));
            } else {
                break;
            }
        }
        for (int i = posA.getRow() - 1; i >= 0; i--) {
            if (gameBoard.getCell(i, posA.getCol()).isEmpty()) {
                res.add(gameBoard.getCell(i, posA.getCol()));
            } else {
                break;
            }
        }
        for (int i = posA.getCol() + 1; i < gameBoard.getColCnt(); i++) {
            if (gameBoard.getCell(posA.getRow(), i).isEmpty()) {
                res.add(gameBoard.getCell(posA.getRow(), i));
            } else {
                break;
            }
        }
        for (int i = posA.getCol() - 1; i >= 0; i--) {
            if (gameBoard.getCell(posA.getRow(), i).isEmpty()) {
                res.add(gameBoard.getCell(posA.getRow(), i));
            } else {
                break;
            }
        }
        return res;
    }
    public static boolean isPathClear(GameBoard gameBoard, Position from, Position to){
        if(from.getRow() == to.getRow()){
            int minCol = Math.min(from.getCol(), to.getCol());
            int maxCol = Math.max(from.getCol(), to.getCol());
            for (int col = minCol+1; col < maxCol ; col++) {
                if (!gameBoard.getCell(from.getRow(), col).isEmpty()) {
                    return false;
                }
            }
            return true;
        }else if (from.getCol() == to.getCol()){
            int minRow = Math.min(from.getRow(), to.getRow());
            int maxRow = Math.max(from.getRow(), to.getRow());
            for (int row = minRow+1; row < maxRow ; row++) {
                if (!gameBoard.getCell(row, from.getCol()).isEmpty()) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    public static boolean findZeroTurn(GameBoard gameBoard, Position posA, Position posB) {
        if(posA.getCol() == posB.getCol() || posA.getRow() == posB.getRow()){
            return isPathClear(gameBoard, posA, posB);
        }
        return false;
    }
    public static boolean findOneTurn(GameBoard gameBoard, Position posA, Position posB) {
        if (posA.getCol() != posB.getCol() && posA.getRow() != posB.getRow()) {
            Position cornerPoint1 = new Position(posA.getRow(), posB.getCol());
            Position cornerPoint2 = new Position(posB.getRow(), posA.getCol());
            if (findZeroTurn(gameBoard, posA, cornerPoint1) && findZeroTurn(gameBoard, posB, cornerPoint1) && (gameBoard.getCell(posA.getRow(), posB.getCol()).isEmpty()) ) {
                return true;
            }
            if (findZeroTurn(gameBoard, posA, cornerPoint2) && findZeroTurn(gameBoard, posB, cornerPoint2) && (gameBoard.getCell(posB.getRow(), posA.getCol()).isEmpty()) ) {
                return true;
            }
        }
        return false;
    }
    public static boolean findTwoTurn(GameBoard gameBoard, Position posA, Position posB) {
        List<Cell> reachablePoints = getReachablePointsInFourDirections(gameBoard, posA);
        for (Cell c: reachablePoints) {
            if (findOneTurn(gameBoard, c.getPos(), posB)) {
                return true;
            }
        }
        return false;
    }

    public static List<Position> findZeroTurnPath(GameBoard gameBoard, Position posA, Position posB){
        if(findZeroTurn(gameBoard, posA, posB)){
            List<Position> path = new ArrayList<>();
            path.add(posA);
            path.add(posB);
            return path;
        }
        return null;
    }
    public static List<Position> findOneTurnPath(GameBoard gameBoard, Position posA, Position posB) {
        if (posA.getCol() != posB.getCol() && posA.getRow() != posB.getRow()) {
            Position cornerPoint1 = new Position(posA.getRow(), posB.getCol());
            Position cornerPoint2 = new Position(posB.getRow(), posA.getCol());
            
            if (gameBoard.getCell(cornerPoint1.getRow(), cornerPoint1.getCol()).isEmpty()) {
                if (findZeroTurn(gameBoard, posA, cornerPoint1) && findZeroTurn(gameBoard, posB, cornerPoint1)) {
                    List<Position> path = new ArrayList<>();
                    path.add(posA);
                    path.add(cornerPoint1);
                    path.add(posB);
                    return path;
                }
            }
            
            if (gameBoard.getCell(cornerPoint2.getRow(), cornerPoint2.getCol()).isEmpty()) {
                if (findZeroTurn(gameBoard, posA, cornerPoint2) && findZeroTurn(gameBoard, posB, cornerPoint2)) {
                    List<Position> path = new ArrayList<>();
                    path.add(posA);
                    path.add(cornerPoint2);
                    path.add(posB);
                    return path;
                }
            }
        }
        return null;
    }
    public static List<Position> findTwoTurnPath(GameBoard gameBoard, Position posA, Position posB) {
        List<Cell> reachablePoints = getReachablePointsInFourDirections(gameBoard, posA);
        for (Cell c: reachablePoints) {
            List<Position> subPath = findOneTurnPath(gameBoard, c.getPos(), posB);
            if(subPath != null){
                List<Position> fullPath = new ArrayList<>();
                fullPath.add(posA);
                fullPath.addAll(subPath);
                return fullPath;
            }
        }
        return null;
    }
    public static List<Position> findPath(GameBoard gameBoard, Position posA, Position posB) {
        List<Position> path = findZeroTurnPath(gameBoard, posA, posB);
        if (path != null) {
            return path;
        }
        path = findOneTurnPath(gameBoard, posA, posB);
        if (path != null) {
            return path;
        }
        path = findTwoTurnPath(gameBoard, posA, posB);
        if(path != null){
            return path;
        }
        return null;
    }

    public static boolean canLinkAB(GameBoard gameBoard, Position posA, Position posB){
        if (findZeroTurn(gameBoard, posA, posB)) {
            return true;
        }
        // 判断1折，检查两个拐点
        if (findOneTurn(gameBoard, posA, posB)) {
            return true;
        }
        // 判断2折
        if (findTwoTurn(gameBoard, posA, posB)) {
            return true;
        }
        return false;
    }
}
