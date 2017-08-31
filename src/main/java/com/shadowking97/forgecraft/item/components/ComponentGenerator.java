package com.shadowking97.forgecraft.item.components;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.shadowking97.forgecraft.client.MapHolder;
import com.shadowking97.forgecraft.client.models.ComponentModel;
import com.shadowking97.forgecraft.item.material.ItemMaterial;
import com.shadowking97.forgecraft.util.JSONUtil;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Stream;

/**
 * Created by Shadow Bolt on 7/22/2017.
 */
public class ComponentGenerator {
    public enum ComponentSlot {
        //Components
        HELMET_CAP,
        HELMET_MASK,
        //Subcomponents - potentially available to all components
        PADDING,
        SOCKET,
        TRIM,
        GEM0,
        GEM1,
        GEM2
    }

    public static ComponentSlot getSlotFromString(String s) {
        if (s.equals("HELMET_CAP"))
            return ComponentSlot.HELMET_CAP;
        if (s.equals("PADDING"))
            return ComponentSlot.PADDING;
        if (s.equals("HELMET_MASK"))
            return ComponentSlot.HELMET_MASK;
        if (s.equals("SOCKET"))
            return ComponentSlot.SOCKET;
        if (s.equals("TRIM"))
            return ComponentSlot.TRIM;
        if (s.equals("GEM") || s.equals("GEM0"))
            return ComponentSlot.GEM0;
        if (s.equals("GEM1"))
            return ComponentSlot.GEM1;
        if (s.equals("GEM2"))
            return ComponentSlot.GEM2;

        System.out.println("Could not get slot for: " + s);

        return null;
    }

    public static float[] slot2Offset(ComponentSlot slot) {
        if (slot != null)
            switch (slot) {
                case HELMET_CAP:
                case HELMET_MASK:
                    return new float[]{-8, -24, -8};
            }

        return new float[]{0, 0, 0};
    }

    HashMap<String, ComponentDefinition> componentDefinitions = new HashMap<String, ComponentDefinition>();

    public static ComponentGenerator INSTANCE = new ComponentGenerator();

    public ComponentDefinition getComponent(String name) {
        if(componentDefinitions.containsKey(name))
            return componentDefinitions.get(name);

        //If getting subcomponents by name is necessary
        /*
        String[] names = name.split(".");

        if(names.length>1)
        {
            ComponentDefinition definition = componentDefinitions.get(names[0]);
            if(definition==null)return null;
            for(int i = 1; i < names.length; i++)
            {
                definition = definition.getSubComponent(names[i]);
                if(definition==null)return null;
            }

            return definition;
        }
        */

        return null;
    }

    public void generateComponents() {
        JsonParser parser = new JsonParser();
        FMLLog.log(Level.INFO, "FORGECRAFT - REGISTERING COMPONENT FILES");

        try {
            URI uri = getClass().getClassLoader().getResource("assets/skforgecraft/components/").toURI();
            Path myPath;
            if (uri.getScheme().equals("jar")) {
                FileSystem fileSystem = FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
                myPath = fileSystem.getPath("assets/skforgecraft/components");
            } else {
                myPath = Paths.get(uri);
            }
            Stream<Path> walk = Files.walk(myPath, 1);
            for (Iterator<Path> it = walk.iterator(); it.hasNext(); ) {
                Path path = it.next();

                if (path.toString().endsWith("json")) {
                    FileReader f = new FileReader(path.toFile());
                    JsonObject component = parser.parse(f).getAsJsonObject();

                    ComponentDefinition definition = loadComponent(component,null);

                    //definitionBuilder.add(definition);
                    componentDefinitions.put(definition.getName(), definition);
                }
            }
        } catch (URISyntaxException e) {
            FMLLog.log(Level.ERROR, e, "FORGECRAFT URI SYNTAX EXCEPTION~~~");
        } catch (IOException e) {
            FMLLog.log(Level.INFO, e, "FORGECRAFT IOException EXCEPTION~~~");
        }
    }

    public ComponentDefinition loadComponent(JsonObject component, String otherName)
    {
        if (!component.has("slot") || !component.has("materialtype") || !component.has("cost") || !component.has("layer") || !component.has("name")) {
            FMLLog.log(Level.ERROR, "Forgecraft - Error defining component, skipping");
            return null;
        }

        String name;

        if(otherName==null||otherName.equals(""))
            name = component.get("name").getAsString();
        else
            name = otherName + "." + component.get("name").getAsString();

        FMLLog.log(Level.INFO, "Registering component: " + name);
        ComponentSlot slot = getSlotFromString(component.get("slot").getAsString());

        HashMap<String, ComponentDefinition> subcomponentMap = new HashMap<>();
        int i = 0;
        while (component.has("subcomponent" + i)) {
            JsonObject subcomponent = JSONUtil.getItemSubcomponent(component.get("subcomponent" + i++).getAsString());

            ComponentDefinition definition = loadComponent(subcomponent,name);
            subcomponentMap.put(subcomponent.get("name").getAsString(), definition);
        }

        ItemMaterial.MaterialType type = ItemMaterial.getTypeFromString(component.get("materialtype").getAsString());
        int cost = component.get("cost").getAsInt();
        int layer = component.get("layer").getAsInt();
        ComponentDefinition definition = new ComponentDefinition(name, slot, type, cost, subcomponentMap, layer, false);
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {

            ResourceLocation baseLocation = null;
            ResourceLocation highlightLocation = null;
            ResourceLocation shadowLocation = null;

            FMLLog.log(Level.INFO, "Adding Textures to component");

            if (component.has("maintex")) {
                String main[] = component.get("maintex").getAsString().split(":");
                if (main.length >= 2) {
                    baseLocation = new ResourceLocation(main[0], main[1]);
                }
            }
            if (component.has("highlight")) {
                String main[] = component.get("highlight").getAsString().split(":");
                if (main.length >= 2) {
                    highlightLocation = new ResourceLocation(main[0], main[1]);
                }
            }
            if (component.has("shadows")) {
                String main[] = component.get("shadows").getAsString().split(":");
                if (main.length >= 2) {
                    shadowLocation = new ResourceLocation(main[0], main[1]);
                }
            }

            definition.addTextures(baseLocation, highlightLocation, shadowLocation);

            //TODO: LOAD MODELS
            ComponentModel model = generateModel(component, slot2Offset(slot));
            definition.setModel(model);
        }

        FMLLog.log(Level.INFO, "Finished parsing component definition: " + name);

        return definition;
    }

    public ComponentModel generateModel(JsonObject component, float[] offset)
    {

        if(component.has("textures")) {

            String[] textureNames;
            TextureAtlasSprite[] baseAtlasTextures;
            TextureAtlasSprite[] highlightAtlasTextures;
            TextureAtlasSprite[] shadowAtlasTextures;

            JsonObject baseTextures = component.get("textures").getAsJsonObject();

            int i = baseTextures.entrySet().size();
            textureNames=new String[i];
            baseAtlasTextures=new TextureAtlasSprite[i];
            highlightAtlasTextures=new TextureAtlasSprite[i];
            shadowAtlasTextures=new TextureAtlasSprite[i];

            for (Map.Entry<String, JsonElement> elementEntry : baseTextures.entrySet()) {
                textureNames[--i]=elementEntry.getKey();
                baseAtlasTextures[i] = MapHolder.textureMapComponents.registerSprite(new ResourceLocation(elementEntry.getValue().getAsString()));
            }

            if (component.has("highlight-textures")) {
                JsonObject highlightTextures = component.get("highlight-textures").getAsJsonObject();
                for (i=0; i<textureNames.length;i++) {
                    if(highlightTextures.has(textureNames[i]))
                        highlightAtlasTextures[i] = MapHolder.textureMapComponents.registerSprite(new ResourceLocation(highlightTextures.get(textureNames[i]).getAsString()));
                }
            }
            if (component.has("shadow-textures")) {
                JsonObject shadowTextures = component.get("shadow-textures").getAsJsonObject();
                for (i=0; i<textureNames.length;i++) {
                    if(shadowTextures.has(textureNames[i]))
                        shadowAtlasTextures[i] = MapHolder.textureMapComponents.registerSprite(new ResourceLocation(shadowTextures.get(textureNames[i]).getAsString()));
                }
            }

            ComponentModel theModel = new ComponentModel(textureNames,baseAtlasTextures,highlightAtlasTextures,shadowAtlasTextures);

            if(component.has("elements"))
            {
                JsonArray array = component.getAsJsonArray("elements");
                for(i=0;i<array.size();i++)
                {
                    JsonObject element = array.get(i).getAsJsonObject();
                    if(element.has("from")&&element.has("to")&&element.has("faces")) {
                        float x1, y1, z1, x2, y2, z2;
                        JsonArray from = element.getAsJsonArray("from");
                        x1 = from.get(0).getAsFloat()+offset[0];
                        y1 = -(from.get(1).getAsFloat()+offset[1]);
                        z1 = -(from.get(2).getAsFloat()+offset[2]);
                        JsonArray to = element.getAsJsonArray("to");
                        x2 = to.get(0).getAsFloat()+offset[0];
                        y2 = -(to.get(1).getAsFloat()+offset[1]);
                        z2 = -(to.get(2).getAsFloat()+offset[2]);

                        ComponentModel.RotationAxis axis = ComponentModel.RotationAxis.AXIS_NONE;
                        float angle = 0;
                        float rotOriginX = 0;
                        float rotOriginY=0;
                        float rotOriginZ=0;

                        if(element.has("rotation")) {
                            JsonObject rotation = element.getAsJsonObject("rotation");
                            String ax = rotation.get("axis").getAsString();
                            if (ax.equals("x"))
                                axis= ComponentModel.RotationAxis.AXIS_X;
                            else if (ax.equals("y"))
                                axis= ComponentModel.RotationAxis.AXIS_Y;
                            else if (ax.equals( "z"))
                                axis= ComponentModel.RotationAxis.AXIS_Z;

                            angle = rotation.get("angle").getAsFloat();

                            JsonArray origin = rotation.getAsJsonArray("origin");
                            rotOriginX=origin.get(0).getAsFloat()+offset[0];
                            rotOriginY=-(origin.get(1).getAsFloat()+offset[1]);
                            rotOriginZ=-(origin.get(2).getAsFloat()+offset[2]);
                        }
                        String[] textures = new String[6];
                        float[] uMin = new float[6];
                        float[] uMax = new float[6];
                        float[] vMin = new float[6];
                        float[] vMax = new float[6];
                        int[] uvRot = {0,0,0,0,0,0};

                        JsonObject faces = element.getAsJsonObject("faces");
                        for (Map.Entry<String, JsonElement> face : faces.entrySet()) {
                            int side = 5;
                            if(Objects.equals(face.getKey(), "north"))
                                side=0;
                            else if(Objects.equals(face.getKey(), "east"))
                                side = 1;
                            else if(Objects.equals(face.getKey(), "south"))
                                side = 2;
                            else if(Objects.equals(face.getKey(), "west"))
                                side = 3;
                            else if(Objects.equals(face.getKey(), "up"))
                                side = 4;

                            JsonObject faceObj = face.getValue().getAsJsonObject();
                            textures[side] = faceObj.get("texture").getAsString();
                            JsonArray faceUV = faceObj.getAsJsonArray("uv");
                            uMin[side] = faceUV.get(0).getAsFloat();
                            vMin[side] = faceUV.get(1).getAsFloat();
                            uMax[side] = faceUV.get(2).getAsFloat();
                            vMax[side] = faceUV.get(3).getAsFloat();

                            if(faceObj.has("rotation"))
                                uvRot[side] = faceObj.get("rotation").getAsInt();
                        }

                        theModel.addElement(x1,y1,z1,x2,y2,z2,textures,uMax,vMax,uMin,vMin,axis,angle,rotOriginX,rotOriginY,rotOriginZ,uvRot);
                    }
                }
            }

            return theModel;
        }
        return null;
    }
}
