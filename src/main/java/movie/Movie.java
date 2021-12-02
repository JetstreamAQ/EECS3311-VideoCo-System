package movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Movie {
    private int id,
                stock;
    private double price;
    private String title,
                   releaseDate,
                   genre,
                   description;

    private List<String> actors,
                         directors,
                         categories;

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public int getStock() {return stock;}
    public void setStock(int stock) {this.stock = stock;}

    public double getPrice() {return price;}
    public void setPrice(double price) {this.price = price;}

    public String getTitle() {return title;}
    public void setTitle(String title) {this.title = title;}

    public String getReleaseDate() {return releaseDate;}
    public void setReleaseDate(String releaseDate) {this.releaseDate = releaseDate;}

    public String getGenre() {return genre;}
    public void setGenre(String genre) {this.genre = genre;}

    public String getDescription() {return description;}
    public void setDescription(String description) {this.description = description;}

    public List<String> getActors() {return actors;}
    public void setActors(ArrayList<String> actors) {this.actors = actors;}

    public List<String> getDirectors() {return directors;}
    public void setDirectors(ArrayList<String> directors) {this.directors = directors;}

    public List<String> getCategories() {return categories;}
    public void setCategories(ArrayList<String> categories) {this.categories = categories;}

    public String toString() {
        return  "Title: " + title + "\n" +
                "Movie ID: " + id + "\n" +
                "Copies in stock: " + stock + "\n" +
                "Price: $" + String.format("%,.2f", price) + "\n" +
                "Release Date: " + releaseDate + "\n" +
                "Genre: " + genre + "\n" +
                "Description: " + description + "\n" +
                "Actors: " + actors.toString().substring(1, actors.toString().length() - 1) + "\n" +
                "Directors: " + directors.toString().substring(1, directors.toString().length() - 1) + "\n" +
                "Categories: " + categories.toString().substring(1, categories.toString().length() - 1);
    }

    public boolean equals(Object o) {
        if (!(o instanceof Movie))
            return false;

        Movie obj = (Movie) o;
        /*
         * Why do we not compare the IDs, stock and price?
         *
         * The IDs are for identifying movies in the system quickly.  While they're a property of Movie, they really
         * are arbitrary numbers which are used for mapping.  It's an internal number to be used by the system for
         * retrieval and searching.  The same applies---to a degree---with stock and price.  Both are internal numbers
         * to be used by the system.
         *
         * Like in real life, two movies can be the same if they have different internal data.  We can have two copies
         * of "xyz" with different IDs, stock numbers and prices.  However, that doesn't change the fact that the two
         * movies are the same copy of each other.
         */
        boolean readableResPrim =   this.title.equals(obj.getTitle()) &&
                                    this.releaseDate.equals(obj.getReleaseDate()) &&
                                    this.genre.equals(obj.getGenre()) &&
                                    this.description.equals(obj.getDescription());
        boolean matchingActors = this.actors.size() == obj.getActors().size();
        if (matchingActors) {
            for (String a : actors)
                matchingActors = matchingActors && obj.getActors().contains(a);
        }

        boolean matchingDirectors = this.directors.size() == obj.getDirectors().size();
        if (matchingDirectors) {
            for (String d : directors)
                matchingDirectors = matchingDirectors && obj.getDirectors().contains(d);
        }

        boolean matchingCategories = this.categories.size() == obj.getCategories().size();
        if (matchingCategories) {
            for (String c : categories)
                matchingCategories = matchingCategories && obj.getCategories().contains(c);
        }

        return readableResPrim && matchingActors && matchingDirectors && matchingCategories;
    }
}
