public class Ship {
    int size;
    int[] coordinatesR;
    int[] coordinatesC;
    boolean[] hit;
    boolean isDestroyed;
    int hitpoints;

    Ship(int size) {
        this.size = size;
        coordinatesR = new int[size];
        coordinatesC = new int[size];
        hit = new boolean[size];
        for (int i = 0; i < size; i++) {
            hit[i] = false;
        }
        isDestroyed = false;
        hitpoints = size;
    }

    void printCoords() {
        for (int i = 0; i < size; i++) {
            System.out.println("Row " + coordinatesR[i] + " Col " + coordinatesC[i] +
                    " Hit: " + hit[i]);
        }
    }

    public boolean isDestroyed() {
        return hitpoints == 0;
    }
}
