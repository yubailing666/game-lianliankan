package model;

import java.util.List;

public class Line {
    List<Position> path;

    public Line(List<Position> path) {
        this.path = path;
    }

    public List<Position> getPath(){
        return path;
    }
}
