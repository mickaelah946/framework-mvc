package com.app.controller;

import com.app.model.Employe;
import com.framework.annotation.Controller;
import com.framework.annotation.HttpMethod;
import com.framework.annotation.UrlMapping;
import com.framework.model.MaSession;
import com.framework.model.ModelAndView;

/** Exemples de creation, lecture et modification des attributs d'une session HTTP. */
@Controller("session")
public class SessionController {

    @UrlMapping("session/compteur")
    public ModelAndView compteur(MaSession session) {
        int visites = nombreVisites(session) + 1;
        session.setAttribute("visites", visites);
        return new ModelAndView("session/result")
                .addAttribut("titre", "Compteur de session")
                .addAttribut("message", "La visite a ete enregistree dans la session.")
                .addAttribut("visites", visites)
                .addAttribut("active", true);
    }

    /** Formulaire de saisie d'un objet qui sera conserve dans la session du navigateur. */
    @UrlMapping("session/form")
    public ModelAndView form(MaSession session) {
        return new ModelAndView("session/form")
                .addAttribut("sessionId", session.getId());
    }

    /** Lie les champs du formulaire a un Employe et le stocke dans cette session. */
    @UrlMapping(value = "session/enregistrer", method = HttpMethod.POST)
    public ModelAndView enregistrer(Employe employe, MaSession session) {
        session.setAttribute("employe", employe);
        int visites = nombreVisites(session);
        return new ModelAndView("session/result")
                .addAttribut("titre", "Objet enregistre dans la session")
                .addAttribut("message", "L'employe a ete enregistre pour cette session de navigateur.")
                .addAttribut("visites", visites)
                .addAttribut("active", true)
                .addAttribut("sessionId", session.getId())
                .addAttribut("employe", employe);
    }

    @UrlMapping("session/etat")
    public ModelAndView etat(MaSession session) {
        return new ModelAndView("session/result")
                .addAttribut("titre", "Etat de la session")
                .addAttribut("message", "La session courante a ete relue.")
                .addAttribut("visites", nombreVisites(session))
                .addAttribut("active", true)
                .addAttribut("sessionId", session.getId())
                .addAttribut("employe", session.getAttribute("employe"));
    }

    @UrlMapping(value = "session/fin", method = HttpMethod.POST)
    public ModelAndView fin(MaSession session) {
        session.removeAttribute("visites");
        session.removeAttribute("employe");
        return new ModelAndView("session/result")
                .addAttribut("titre", "Compteur remis a zero")
                .addAttribut("message", "Les attributs de demonstration ont ete supprimes.")
                .addAttribut("visites", 0)
                .addAttribut("active", true)
                .addAttribut("sessionId", session.getId());
    }

    private int nombreVisites(MaSession session) {
        Object value = session.getAttribute("visites");
        return value instanceof Integer ? (Integer) value : 0;
    }
}
