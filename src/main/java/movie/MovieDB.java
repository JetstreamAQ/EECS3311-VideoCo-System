package movie;

import org.yaml.snakeyaml.Yaml;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class MovieDB {
    /**HashMap storing all movies by their ID**/
    private Map<Integer, Movie> movieDB = new HashMap<>();

    /**ArrayList used for faster addition of movies**/
    private ArrayList<Movie> movieArray = new ArrayList<>();

    /**Singleton instance**/
    private static final MovieDB INSTANCE = new MovieDB();

    /**Grabbing the singleton instance**/
    public static MovieDB getINSTANCE() {
        return INSTANCE;
    }

    private MovieDB() {
        loadYAML();
    }

    /**
     * Grabs information from the YAML file; storing grabbed data in a HashMap
     */
    private void loadYAML() {
        //Flushing the HashMap and ArrayList
        movieDB = new HashMap<>();
        movieArray = new ArrayList<>();

        Yaml yaml = new Yaml();

        InputStream is = null;
        try {
            is = Objects.requireNonNull(getClass().getClassLoader().getResource("yaml/movies.yml")).openStream();
        } catch (IOException e) {
            System.out.println("[MovieDB]: DB Loading Error\n" + e.getMessage());
        }

        Movies movies = yaml.loadAs(is, Movies.class);
        movieArray.addAll(movies.getMovies());

        for (Movie m : movies.getMovies())
            movieDB.put(m.getId(), m);
    }

    /**
     * Stores data back into the YAML file
     */
    private void writeYAML() {
        //Loading the YAML file for writing
        FileWriter fw = null;
        try {
            fw = new FileWriter("src/main/resources/yaml/movies.yml");
        } catch (IOException e) {
            System.out.println("[MovieDB]: DB File Error\n" + e.getMessage());
        }

        Yaml yaml = new Yaml();
        Movies movies = new Movies();
        movies.setMovies(movieArray);
        yaml.dump(movies, fw);
    }

    /**
     * Add a movie to the DB and update the DB file
     *
     * @param movie the movie object to add
     * @return  true if the movie was added successfully
     *          false if the movie to add has an existing ID or if the movie already exists in the DB
     */
    public boolean addMovie(Movie movie) {
        //check if the added movie has an ID conflict!
        if (movieDB.containsKey(movie.getId()))
            return false;

        //Check if the added movie already exists
        if (movieArray.contains(movie))
            return false;

        //Adding the movie to the DB and updating the DB file
        movieArray.add(movie);
        movieDB.put(movie.getId(), movie);
        writeYAML();
        return true;
    }

    /**
     * Remove a movie from the DB and update the DB file
     *
     * @param id the ID number of the movie to remove
     * @return  true if the movie was successfully removed
     *          false if the movie does not exist in the DB
     */
    public boolean removeMovie(int id) {
        //Check to see if the id exists in the DB; do nothing if id DNE
        if (!movieDB.containsKey(id))
            return false;

        //Removing the movie from the DB and updating the DB file
        movieArray.remove(movieDB.get(id));
        movieDB.remove(id);
        writeYAML();
        return true;
    }

    /**
     * Searches the movieDB map for the desired movie using it's ID.
     *
     * @param id the integer ID of the movie
     * @return movie with the matching ID; returns null if ID doesn't exist in DB
     */
    public Movie getMovie(int id) {
        //Check if DB contains movie; if so return it.
        if (movieDB.containsKey(id))
            return movieDB.get(id);

        //DB doesn't contain movie.
        System.out.println("[MovieDB]: No matching IDs.");
        return null;
    }

    /**
     * Search the movie DB matching 'search' based on the passed flag.  Search match requirements are lenient.  As long
     * as the search query is found as a substring in the specified flag property, it will be marked as a match and
     * returned with whatever else matches.
     *
     * @param search the search term(s) to use.  Terms are split up by semi-colons, followed by a space, when
     *               searching by actors, directors and categories.
     * @param flag denotes the type of search to perform depending on the passed integers.
     *             - 0 = title
     *             - 1 = release date
     *             - 2 = genre
     *             - 3 = description
     *             - 4 = actors
     *             - 5 = directors
     *             - 6 = categories
     * @return an ArrayList containing all the movies which matched the given search string
     * @throws IllegalArgumentException when flag < 0 OR flag > 6
     */
    public ArrayList<Movie> getMovie(String search, int flag) {
        if (flag < 0 || flag > 6)
            throw new IllegalArgumentException();

        ArrayList<Movie> matches = new ArrayList<>();

        //check if the DB contains a movie/movies with the matching search terms
        for (Movie m : movieArray) {
            String focus = null;
            switch(flag) {
                case 0: focus = m.getTitle(); break;
                case 1: focus = m.getReleaseDate(); break;
                case 2: focus = m.getGenre(); break;
                case 3: focus = m.getDescription(); break;

                //Ignore focus for 4-6; have to search through arrays here
                case 4:
                case 5:
                case 6:
                    List<String> focusList =    (flag == 4) ?   m.getActors() :
                                                (flag == 5) ?   m.getDirectors() :
                                                                m.getCategories();
                    String[] searchTerms = search.split("; "); //splitting up the search terms
                    for (String term : searchTerms) {
                        for (String s : focusList) {
                            if (!matches.contains(m) && s.equals(term)) {
                                matches.add(m);
                            }
                        }
                    }

                    break;
            }

            //Focus isn't set for flag #4-6; this won't execute for those flags
            if (focus != null && focus.toLowerCase().contains(search.toLowerCase()))
                matches.add(m);
        }

        return matches;
    }

    public ArrayList<Movie> getMovies() {return movieArray;}

    /**
     * Set the stock count of the specified movie
     *
     * @param id the ID of the movie
     * @param newStock the new stock of the movie mapped to the given ID
     * @return  0 when the stock count has been successfully set
     *          1 if no movie exists with the given ID
     *          2 if newStock == currentStock
     * @throws IllegalArgumentException when newStock < 0
     */
    public int setStock(int id, int newStock) {
        if (newStock < 0)
            throw new IllegalArgumentException();

        if (!movieDB.containsKey(id))
            return 1;
        if (movieDB.get(id).getStock() == newStock)
            return 2;

        movieDB.get(id).setStock(newStock);
        return 0;
    }

    /**
     * Set the price of the specified movie
     *
     * @param id of the movie whose price will be changed
     * @param newPrice the new price of the movie
     * @return  0 if the price has been set
     *          1 if no movie exists with the given ID
     *          2 if newPrice == currentPrice
     * @throws IllegalArgumentException when newPrice < 0
     */
    public int setPrice(int id, double newPrice) {
        if (newPrice < 0)
            throw new IllegalArgumentException();

        if (!movieDB.containsKey(id))
            return 1;
        if (movieDB.get(id).getPrice() == newPrice)
            return 2;

        movieDB.get(id).setPrice(newPrice);
        return 0;
    }

    /**
     * Set either the Title, Release Date, Genre or Description of a specified movie depending on the
     * flag passed.
     *
     * @param id the id of the movie
     * @param flag the flag indicating which property of the movie is being set
     *              - 0: title
     *              - 1: releaseDate
     *              - 2: genre
     *              - 3: description
     * @param newString the string to set the specified property to
     * @return  0 when the specified property has been set
     *          1 when there exists no movie with the specified ID
     *          2 when the new property is the same as the current property
     *          3 if when modifying the release date, the format is invalid.  Format: YYYY-MM-DD
     *          4 <- only here for completeness sake; this should never be reached
     * @throws IllegalArgumentException when newString is null or an empty string; when !(0 <= flag <= 3)
     */
    public int setTRGD(int id, int flag, String newString) {
        if (newString == null || newString.equals("") || flag > 3 || flag < 0)
            throw new IllegalArgumentException();

        if (!movieDB.containsKey(id))
            return 1;

        String specProperty =   (flag == 0)     ?   movieDB.get(id).getTitle() :
                                (flag == 1)     ?   movieDB.get(id).getReleaseDate() :
                                (flag == 2)     ?   movieDB.get(id).getGenre() :
                                                    movieDB.get(id).getDescription();
        if (specProperty.equals(newString))
            return 2;

        switch (flag) {
            case 0: movieDB.get(id).setTitle(newString); writeYAML(); return 0;

            case 1:
                if (!newString.matches("^\\d{4}-[0, 1][1-9]-[0-3]\\d$"))
                    return 3;

                movieDB.get(id).setReleaseDate(newString);
                writeYAML();
                return 0;

            case 2: movieDB.get(id).setGenre(newString); writeYAML(); return 0;
            case 3: movieDB.get(id).setDescription(newString); writeYAML(); return 0;
        }

        return 4; //This shouldn't be reached
    }

    /**
     * Set either the list of Actors, Directors or Categories of a specified movie depending on the passed
     * flag.
     *
     * @param id the id of the moive
     * @param flag the flag denoting which property list to set
     *              - 0: actors
     *              - 1: directors
     *              - 2: categories
     * @param propNum the index of the property to change
     * @param newString the string to set the specified property to
     * @return  0 if the specified property has been set
     *          1 if there exists no movie with the specified ID
     *          2 if there exists a property in the list that is the same as newString
     * @throws IllegalArgumentException
     *          when newString is null or an empty string; when !(0 <= flag <= 2); when propNum > propertyList.size()
     */
    public int setADC(int id, int flag, int propNum, String newString) {
        if (newString == null || newString.equals("") || flag > 2 || flag < 0)
            throw new IllegalArgumentException();

        if (!movieDB.containsKey(id))
            return 1;

        List<String> propList =     (flag == 0)     ?   movieDB.get(id).getActors() :
                                    (flag == 1)     ?   movieDB.get(id).getDirectors() :
                                                        movieDB.get(id).getCategories();
        if (propNum > propList.size())
            throw new IllegalArgumentException();

        if (propList.contains(newString))
            return 2;

        if (propNum > propList.size() - 1)
            propList.add(newString);
        else
            propList.set(propNum, newString);

        writeYAML();
        return 0;
    }
}
