package world;

public class Tile {
    public static Tile tiles[] = new Tile[16];
    public static byte numTiles = 0;

    public static final Tile test_tile = new Tile("BrickTest");
    public static final Tile test2 = new Tile("BlueBrickTest").setSolid(true);

    private byte id;
    private boolean solid;
    private String texture;

    public Tile(String texture) {
        this.id = numTiles;
        numTiles++;
        this.texture = texture;
        this.solid = false;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tiles at [" + id + "] is already being used");
        }
        tiles[id] = this;
    }

    public Tile setSolid(boolean solid) {
        this.solid = solid;
        return this;
    }

    public boolean isSolid() {return solid;}

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}
