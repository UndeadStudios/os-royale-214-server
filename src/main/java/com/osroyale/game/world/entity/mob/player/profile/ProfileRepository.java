package com.osroyale.game.world.entity.mob.player.profile;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.osroyale.game.world.entity.mob.player.Player;
import com.osroyale.util.GsonUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static java.nio.file.Paths.*;

/**
 * Handles the profile repository, used for gathering important information for
 * all created profiles.
 *
 * @author Daniel
 */
public class ProfileRepository {

    /** The hash map of all the profiles. */
    private static Map<String, Profile> PROFILES = new HashMap<>();

    /** Checks if a profile is registered to the parameter. */
    public static boolean exist(String name) {
        return PROFILES.containsKey(name);
    }

    /** Checks if the other player is a friend. */
    public static boolean isFriend(Player player, String other) {
        return player.relations.isFriendWith(other);
    }

    /** Gets all the registered accounts to a specific host. */
    public static List<String> getRegistry(String host) {
        List<String> list = new ArrayList<>();
        for (Profile profile : PROFILES.values()) {
            for (String host_list : profile.getHost()) {
                if (host_list != null && host_list.equalsIgnoreCase(host)) {
                    list.add(profile.getName());
                }
            }
        }
        return list;
    }

    /** Puts a profile into the hash map. */
    public static void put(Profile profile) {
        if (PROFILES.containsKey(profile.getName())) {
            PROFILES.replace(profile.getName(), profile);
        } else {
            PROFILES.put(profile.getName(), profile);
        }
        save();

    }

    /** Loads all the profiles. */
    public static void load() {
        Type type = new TypeToken<Map<String, Profile>>() {
        }.getType();
        Path path = get("data", "/profile/world_profile_list.json");
        if (!Files.exists(path)) {
            return;
        }
        try (FileReader reader = new FileReader(path.toFile())) {
            JsonParser parser = new JsonParser();
            PROFILES = new GsonBuilder().create().fromJson(parser.parse(reader), type);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**  Saves all the profiles. */
    public static void save() {
        new Thread(() -> {
            File dir = get("data", "profile").toFile();

            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path path = dir.toPath().resolve("world_profile_list.json");

            try (FileWriter fw = new FileWriter(path.toFile())) {
                fw.write(GsonUtils.JSON_PRETTY_NO_NULLS.toJson(PROFILES));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
