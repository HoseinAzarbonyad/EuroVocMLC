/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.uva.MLC;

import java.util.Objects;

/**
 *
 * @author mosi
 */
public class Doc {
    
    private String id;
    private String n;
    private String lang;
    private String creationDate;
    private String title;
    private String url;
    private String note;
    private String text;

    public Doc(String id, String n, String lang, String creationDate, String title, String url, String note, String text) {
        this.id = id;
        this.n = n;
        this.lang = lang;
        this.creationDate = creationDate;
        this.title = title;
        this.url = url;
        this.note = note;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getN() {
        return n;
    }

    public String getLang() {
        return lang;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getNote() {
        return note;
    }

    public String getText() {
        return text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Doc other = (Doc) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }   
}
