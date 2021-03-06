package de.gurkenlabs.litiengine.environment.tilemap.xml;

import java.awt.Point;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import de.gurkenlabs.litiengine.environment.tilemap.ITerrain;
import de.gurkenlabs.litiengine.environment.tilemap.ITile;
import de.gurkenlabs.litiengine.environment.tilemap.ITileAnimation;
import de.gurkenlabs.litiengine.util.ArrayUtilities;

@XmlRootElement(name = "tile")
@XmlAccessorType(XmlAccessType.FIELD)
public class Tile extends CustomPropertyProvider implements ITile, Serializable {
  public static final int NONE = 0;
  public static final Tile EMPTY = new Tile(NONE);
  protected static final int FLIPPED_HORIZONTALLY_FLAG = 0x80000000;
  protected static final int FLIPPED_VERTICALLY_FLAG = 0x40000000;
  protected static final int FLIPPED_DIAGONALLY_FLAG = 0x20000000;

  private static final long serialVersionUID = -7597673646108642906L;

  @XmlAttribute
  private Integer gid;

  @XmlAttribute
  private Integer id;

  @XmlAttribute
  private String terrain;

  @XmlElement(required = false)
  private Animation animation;

  private transient Point tileCoordinate;

  private transient ITerrain[] terrains;

  private transient ITile customPropertySource;

  private transient boolean flippedDiagonally;
  private transient boolean flippedHorizontally;
  private transient boolean flippedVertically;
  private transient boolean flipped;

  public Tile() {
  }

  public Tile(int gidBitmask) {
    // Clear the flags
    this.flippedDiagonally = (gidBitmask & FLIPPED_DIAGONALLY_FLAG) != 0;
    this.flippedHorizontally = (gidBitmask & FLIPPED_HORIZONTALLY_FLAG) != 0;
    this.flippedVertically = (gidBitmask & FLIPPED_VERTICALLY_FLAG) != 0;

    this.flipped = this.isFlippedDiagonally() || this.isFlippedHorizontally() || this.isFlippedVertically();
    this.gid = gidBitmask & ~(FLIPPED_HORIZONTALLY_FLAG | FLIPPED_VERTICALLY_FLAG | FLIPPED_DIAGONALLY_FLAG);
  }

  @Override
  public boolean hasCustomProperty(String name) {
    return customPropertySource == null ? super.hasCustomProperty(name) : customPropertySource.hasCustomProperty(name);
  }

  @Override
  public List<Property> getProperties() {
    return customPropertySource == null ? super.getProperties() : customPropertySource.getProperties();
  }

  @Override
  public void setProperties(List<Property> props) {
    if (customPropertySource == null) {
      super.setProperties(props);
    } else {
      customPropertySource.setProperties(props);
    }
  }

  @Override
  public String getStringValue(String name, String defaultValue) {
    return customPropertySource == null ? super.getStringValue(name, defaultValue) : customPropertySource.getStringValue(name, defaultValue);
  }

  @Override
  public void setValue(String name, String value) {
    if (customPropertySource == null) {
      super.setValue(name, value);
    } else {
      customPropertySource.setValue(name, value);
    }
  }

  @Override
  public boolean isFlippedDiagonally() {
    return this.flippedDiagonally;
  }

  @Override
  public boolean isFlippedHorizontally() {
    return this.flippedHorizontally;
  }

  @Override
  public boolean isFlippedVertically() {
    return this.flippedVertically;
  }

  @Override
  public boolean isFlipped() {
    return this.flipped;
  }

  @Override
  public int getGridId() {
    if (this.gid == null) {
      return 0;
    }

    return this.gid;
  }

  @Override
  public Point getTileCoordinate() {
    return this.tileCoordinate;
  }

  /**
   * Sets the tile coordinate.
   *
   * @param tileCoordinate
   *          the new tile coordinate
   */
  public void setTileCoordinate(final Point tileCoordinate) {
    this.tileCoordinate = tileCoordinate;
  }

  @Override
  public int getId() {
    if (this.id == null) {
      return 0;
    }

    return this.id;
  }

  @Override
  public ITerrain[] getTerrain() {
    return customPropertySource == null ? this.terrains : customPropertySource.getTerrain();
  }

  @Override
  public ITileAnimation getAnimation() {
    return customPropertySource == null ? this.animation : customPropertySource.getAnimation();
  }

  @Override
  public String toString() {
    for (int i = 0; i < this.getTerrainIds().length; i++) {
      if (this.getTerrainIds()[i] != Terrain.NONE) {
        return this.getGridId() + " (" + Arrays.toString(this.getTerrainIds()) + ")";
      }
    }

    return this.getGridId() + "";
  }

  protected int[] getTerrainIds() {
    int[] terrainIds = new int[] { Terrain.NONE, Terrain.NONE, Terrain.NONE, Terrain.NONE };
    if (this.terrain == null || this.terrain.isEmpty()) {
      return terrainIds;
    }

    int[] ids = ArrayUtilities.getIntegerArray(this.terrain);
    if (ids.length != 4) {
      return terrainIds;
    } else {
      terrainIds = ids;
    }

    return terrainIds;
  }

  protected void setTerrains(ITerrain[] terrains) {
    this.terrains = terrains;
  }

  protected void setCustomPropertySource(ITile source) {
    customPropertySource = source;
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller u, Object parent) {
    if (this.gid != null && this.gid == 0) {
      this.gid = null;
    }

    if (this.id != null && this.id == 0) {
      this.id = null;
    }
  }
}
