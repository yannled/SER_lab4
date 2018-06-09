package controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import models.*;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class JsonCreation {
    private GlobalData globalData;

    public JsonCreation(GlobalData globalData) {
        this.globalData = globalData;
    }

    public void create() {

        JsonObject projectionsObject = new JsonObject();
        JsonArray projections = new JsonArray();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // parcours de toutes les projections et création des objets Json pour chacune d'elle
        for (Projection projection : globalData.getProjections()) {

            JsonObject projectionObject = new JsonObject();
            JsonObject actorObject = new JsonObject();

            projectionObject.addProperty("TitreFilm", projection.getFilm().getTitre());

            // Formater la date
            SimpleDateFormat format1 = new SimpleDateFormat("dd-mm-yyyy");
            String projectionDate = format1.format(projection.getDateHeure().getTime());
            projectionObject.addProperty("DateProjection", projectionDate);

            // Récupére le 1er et 2èeme Role
            int i = 0;
            RoleActeur role1 = new RoleActeur();
            RoleActeur role2  = new RoleActeur();

            for(RoleActeur role : projection.getFilm().getRoles()){
                if(role.getPlace() == 1){
                    role1 = role;
                    i++;
                }
                if(role.getPlace() == 2) {
                    role2 = role;
                    i++;
                }
                if(i >= 2)
                    break;
            }

            actorObject.addProperty("1er Role", role1.getActeur().getNom());
            actorObject.addProperty("2eme Role", role2.getActeur().getNom());

            // ajout de l'objet acteur à la projection
            projectionObject.add("Acteurs", actorObject);

            // ajout de l'objet projeciton à la list de projection
            projections.add(projectionObject);

        }

        //ajout de la liste de projection à l'objet Projections
        projectionsObject.add("Projections", projections);


        try (FileWriter writer = new FileWriter("json.json")) {

            gson.toJson(projectionsObject, writer);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
