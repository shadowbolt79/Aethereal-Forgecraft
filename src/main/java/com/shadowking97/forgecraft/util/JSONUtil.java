package com.shadowking97.forgecraft.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowking97.forgecraft.item.components.ComponentGenerator;

import java.io.FileReader;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Created by Shadow Bolt on 7/22/2017.
 */
@SuppressWarnings("Since15")
public class JSONUtil {

    public static JsonObject getItemSubcomponent(String subcomponentName)
    {
        try {
            URI uri = ComponentGenerator.INSTANCE.getClass().getClassLoader().getResource("assets/skforgecraft/subcomponents/" + subcomponentName +".json").toURI();
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath("assets/skforgecraft/subcomponents/" + subcomponentName +".json");
            } else {
                myPath = Paths.get(uri);
            }
            FileReader f = new FileReader(myPath.toFile());
            JsonParser parser = new JsonParser();
            return parser.parse(f).getAsJsonObject();
        } catch(Exception e){
            CommonUtil.error("Encountered exception while loading subcomponent: "+subcomponentName, e);
        }
        return null;
    }
}
