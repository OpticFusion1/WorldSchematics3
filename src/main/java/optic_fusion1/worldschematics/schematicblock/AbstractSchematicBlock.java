package optic_fusion1.worldschematics.schematicblock;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

public class AbstractSchematicBlock {

    private Location schematicLocation;
    private ConfigurationSection configSection;
    private String name;

    public AbstractSchematicBlock(Location schematicLocation, ConfigurationSection configSection, String name) {
        this.schematicLocation = schematicLocation;
        this.configSection = configSection;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Location getLocation() {
        return schematicLocation;
    }

    public void setLocation(Location location) {
        this.schematicLocation = location;
    }

    public enum BlockType {
        SPAWNER, CONTAINER, MARKER
    }

    public ConfigurationSection configSection() {
        return configSection;
    }

}
