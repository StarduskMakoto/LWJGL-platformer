package world;

public class Tile {
    public static Tile tiles[] = new Tile[16];
    public static byte numTiles = 0;

    public static final Tile test_tile = new Tile("SimpleSquareTexture");
    public static final Tile test2 = new Tile("Tile2Texture");

    private byte id;
    private String texture;

    public Tile(String texture) {
        this.id = numTiles;
        numTiles++;
        this.texture = texture;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tiles at [" + id + "] is already being used");
        }
        tiles[id] = this;
    }

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}
