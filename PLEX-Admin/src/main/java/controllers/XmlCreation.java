package controllers;

import models.*;
import org.dom4j.DocumentType;
import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import views.*;

import java.io.*;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;


import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public class XmlCreation {
    private GlobalData globalData;
    private List<Film> films;
    private List<Acteur> acteurs;
    private List<Motcle> motsCles;
    private List<Critique> critiques;
    private List<Langage> langages;
    private List<Genre> genres;

    public XmlCreation(GlobalData globalData) {
        this.globalData = globalData;
        films = getAllFilms();
        acteurs = getAllActors();
        motsCles = getAllMotsCle();
        critiques = getAllCrittiques();
        langages = getAllLanguages();
        genres = getAllGenres();
    }

    public void create() {
        Document document = new Document();
        Element racine = new Element("cinema");
        DocType docktype = new DocType(racine.getName());
        docktype.setSystemID("xml.dtd");
        document.setDocType(docktype);


        document.addContent(racine);

        // AJOUTS PROJECTIONS

        Element e_projections = new Element("projections");
        racine.addContent(e_projections);

        for (Projection projection : globalData.getProjections()) {
            Element e_projection = new Element("projection");
            e_projections.addContent(e_projection);

            Element e_dateProjection = new Element("dateProjection");
            e_projection.addContent(e_dateProjection);
            e_dateProjection.setText(projection.getDateHeure().getTime().toString());

            Element e_numeroSalle = new Element("numeroSalle");
            e_projection.addContent(e_numeroSalle);
            e_numeroSalle.setText(String.valueOf(projection.getSalle()));

            Element e_filmProj = new Element("filmProj");
            e_projection.addContent(e_filmProj);
            e_filmProj.setAttribute("filmId", "id_" + String.valueOf(projection.getFilm().getId()));

            Element e_acteursProj = new Element("acteursProj");
            e_projection.addContent(e_acteursProj);

            for (RoleActeur role : projection.getFilm().getRoles()) {

                Element e_acteurProj = new Element("acteurProj");
                e_acteursProj.addContent(e_acteurProj);
                Attribute a1 = new Attribute("acteurProjId", "id_" +  String.valueOf(role.getActeur().getId()));
                Attribute a2 = new Attribute("nomRole", role.getPersonnage());
                Attribute a3 = new Attribute("placeRole", String.valueOf(role.getPlace()));
                List<Attribute> attr = new ArrayList<>();
                attr.add(a1);
                attr.add(a2);
                attr.add(a3);
                e_acteurProj.setAttributes(attr);
            }

        }


        // AJOUTS FILMS

        Element e_films = new Element("films");
        racine.addContent(e_films);

        for (Film film : films){

            Element e_film = new Element("film");
            e_films.addContent(e_film);
            e_film.setAttribute("filmId","id_" + String.valueOf(film.getId()));

            Element e_titre = new Element("titre");
            e_film.addContent(e_titre);
            e_titre.setText(film.getTitre());

            Element e_synopsis = new Element("synopsis");
            e_film.addContent(e_synopsis);
            e_synopsis.setText(film.getSynopsis());

            Element e_duree = new Element("duree");
            e_film.addContent(e_duree);
            e_duree.setText(String.valueOf(film.getDuree()));

            for(Critique critique : film.getCritiques()){
                Element e_critiqueFilm = new Element("critiqueFilm");
                e_film.addContent(e_critiqueFilm);
                e_critiqueFilm.setAttribute("critiqueFilmId", "id_" + String.valueOf(critique.getId()));
            }

            Element e_genresFilm = new Element("genresFilm");
            e_film.addContent(e_genresFilm);
            for(Genre genre : film.getGenres()){
                Element e_genreFilm = new Element("genreFilm");
                e_genresFilm.addContent(e_genreFilm);
                e_genreFilm.setAttribute("genreFilmId", "id_" + String.valueOf(genre.getId()));
            }

            Element e_motsCleFilm = new Element("motsCleFilm");
            e_film.addContent(e_motsCleFilm);
            for(Motcle mots: film.getMotcles()){
                Element e_motCleFilm = new Element("motCleFilm");
                e_motsCleFilm.addContent(e_motCleFilm);
                e_motCleFilm.setAttribute("motCleFilmId", "id_" + String.valueOf(mots.getId()));
            }

            Element e_langagesFilm = new Element("langagesFilm");
            e_film.addContent(e_langagesFilm);
            for(Langage langue: film.getLangages()){
                Element e_langageFilm = new Element("langageFilm");
                e_langagesFilm.addContent(e_langageFilm);
                e_langageFilm.setAttribute("langageFilmId","id_" +  String.valueOf(langue.getId()));
            }

            Element e_photo = new Element("photo");
            e_film.addContent(e_photo);
            if(film.getPhoto() != null)
                e_photo.setAttribute("url", film.getPhoto());
            else
                e_photo.setAttribute("url", "#NoPhotoExist");

        }

        // AJOUTS ACTEURS

        Element e_acteurs = new Element("acteurs");
        racine.addContent(e_acteurs);

        for(Acteur acteur : acteurs){
            Element e_acteur = new Element("acteur");
            e_acteurs.addContent(e_acteur);
            e_acteur.setAttribute("acteurId", "id_" + String.valueOf(acteur.getId()));

            Element e_nom = new Element("nom");
            e_acteur.addContent(e_nom);
            e_nom.setText(acteur.getNom());

            Element e_nomNaissance = new Element("nomNaissance");
            e_acteur.addContent(e_nomNaissance);
            e_nomNaissance.setText(acteur.getNomNaissance());

            Element e_biographie = new Element("biographie");
            e_acteur.addContent(e_biographie);
            e_biographie.setText(acteur.getBiographie());

            Element e_sexe = new Element("sexe");
            e_acteur.addContent(e_sexe);
            e_sexe.setAttribute("type",acteur.getSexe().name());

            Element e_dateNaissance = new Element("dateNaissance");
            e_acteur.addContent(e_dateNaissance);
            if(acteur.getDateNaissance() != null)
                e_dateNaissance.setText(acteur.getDateNaissance().getTime().toString());
            else
                e_dateNaissance.setText("???");

            Element e_dateDeces = new Element("dateDeces");
            e_acteur.addContent(e_dateDeces);
            if(acteur.getDateDeces() != null)
                e_dateDeces.setText(acteur.getDateDeces().getTime().toString());
            else
                e_dateDeces.setText("???");
        }

        // AJOUTS MOTSCLE

        Element e_motsCle = new Element("motsCle");
        racine.addContent(e_motsCle);

        for(Motcle mot : motsCles){
            Element e_motCle = new Element("motCle");
            e_motsCle.addContent(e_motCle);
            e_motCle.setAttribute("motCleId", "id_" + String.valueOf(mot.getId()));

            Element e_labelMc = new Element("labelMc");
            e_motCle.addContent(e_labelMc);
            e_labelMc.setText(mot.getLabel());
        }

        // AJOUTS GENRES

        Element e_genres = new Element("genres");
        racine.addContent(e_genres);

        for(Genre genre : genres){
            Element e_genre = new Element("genre");
            e_genres.addContent(e_genre);
            e_genre.setAttribute("genreId","id_" +  String.valueOf(genre.getId()));

            Element e_labelGe = new Element("labelGe");
            e_genre.addContent(e_labelGe);
            e_labelGe.setText(genre.getLabel());
        }

        // AJOUTS LANGAGES

        Element e_langages = new Element("langages");
        racine.addContent(e_langages);

        for(Langage langage : langages){
            Element e_langage = new Element("langage");
            e_langages.addContent(e_langage);
            e_langage.setAttribute("langageId", "id_" + String.valueOf(langage.getId()));

            Element e_labelLa = new Element("labelLa");
            e_langage.addContent(e_labelLa);
            e_labelLa.setText(langage.getLabel());
        }

        // AJOUTS CRITIQUES

        Element e_critiques = new Element("critiques");
        racine.addContent(e_critiques);

        for(Critique critique : critiques){
            Element e_critique = new Element("critique");
            e_critiques.addContent(e_critique);
            e_critique.setAttribute("critiqueId","id_" +  String.valueOf(critique.getId()));

            Element e_texte = new Element("texte");
            e_critique.addContent(e_texte);
            e_texte.setText(critique.getTexte());

            Element e_note = new Element("note");
            e_critique.addContent(e_note);
            e_note.setText(String.valueOf(critique.getNote()));
        }

        saveFile(document);
    }

    private List<Film> getAllFilms() {

        List<Film> films = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            Film film = proj.getFilm();
            if (!films.contains(film))
                films.add(proj.getFilm());
        }

        return films;
    }

    private List<Acteur> getAllActors() {

        List<Acteur> acteurs = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            for (RoleActeur role : proj.getFilm().getRoles()) {

                Acteur acteur = role.getActeur();
                if (!acteurs.contains(acteur))
                    acteurs.add(acteur);
            }
        }

        return acteurs;
    }

    private List<Motcle> getAllMotsCle() {

        List<Motcle> motsCles = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            Set<Motcle> mots = proj.getFilm().getMotcles();

            for (Motcle mot : mots) {

                if (!motsCles.contains(mot))
                    motsCles.add(mot);
            }
        }

        return motsCles;
    }

    private List<Critique> getAllCrittiques() {

        List<Critique> critiques = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            Set<Critique> crits = proj.getFilm().getCritiques();

            for (Critique crit : crits) {

                if (!critiques.contains(crit))
                    critiques.add(crit);
            }
        }

        return critiques;
    }

    private List<Langage> getAllLanguages() {

        List<Langage> languages = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            Set<Langage> langs = proj.getFilm().getLangages();

            for (Langage lang : langs) {

                if (!languages.contains(lang))
                    languages.add(lang);
            }
        }

        return languages;
    }

    private List<Genre> getAllGenres() {

        List<Genre> genres = new ArrayList<>();

        for (Projection proj : globalData.getProjections()) {

            Set<Genre> gs = proj.getFilm().getGenres();

            for (Genre g : gs) {

                if (!genres.contains(g))
                    genres.add(g);
            }
        }

        return genres;
    }

    private void saveFile(Document doc){
        XMLOutputter outp = new XMLOutputter(Format.getPrettyFormat());
        File file = new File("xml.xml");
        try {
            outp.output(doc, new FileOutputStream(file));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}