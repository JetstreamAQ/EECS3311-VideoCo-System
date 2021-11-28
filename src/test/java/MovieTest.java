import movie.Movie;
import movie.MovieDB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

public class MovieTest {
    MovieDB movies = MovieDB.getINSTANCE();
    Movie newMovie = new Movie();

    @Before
    public void set_up() {
        System.out.println("IMPORTANT");
        for (int i = 0; i < 10; i++)
            System.out.println("IF ANY OF THE TESTS FAILS RUN 'loadbkup.sh' IN src/resources/yaml BEFORE RUNNING TESTS AGAIN!");
        System.out.println();

        //Setting the movie properties!
        newMovie.setId(-495);
        newMovie.setStock(789);
        newMovie.setPrice(8.99);
        newMovie.setTitle("Through the Gates of the Silver Key");
        newMovie.setReleaseDate("2077-06-16");
        newMovie.setGenre("Fantasy");
        newMovie.setDescription("Movie adaptation of Lovecraft's work of the same name.  ngl this adaptation sucks");
        ArrayList<String> actors = new ArrayList<>();
        actors.add("A dude in a speedo");
        newMovie.setActors(actors);
        ArrayList<String> directors = new ArrayList<>();
        directors.add("Some person we found in the streets");
        directors.add("Bob");
        directors.add("An underpaid intern");
        newMovie.setDirectors(directors);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("hand_holding");
        newMovie.setCategories(categories);
    }

    @Test
    public void test_movie_loaded() {
        //Won't check how many movies are loaded since this can change as development goes on.
        Movie movie = movies.getMovie(-1);

        //Checking each portion loaded properly
        Assert.assertEquals(-1, movie.getId()); //should obviously pass.
        Assert.assertEquals(100, movie.getStock());
        Assert.assertEquals(3.50, movie.getPrice(), 0);
        Assert.assertEquals("The Crinj Tri-Tachyon", movie.getTitle());
        Assert.assertEquals("2020-04-20", movie.getReleaseDate());
        Assert.assertEquals("Testing", movie.getGenre());
        Assert.assertEquals("Movie about the crinj Tri-Tachyon Corporation.", movie.getDescription());
        Assert.assertEquals(3, movie.getActors().size()); //Should only be 3 actors loaded in
        Assert.assertEquals("Crinj Tri-Tach Employee #21", movie.getActors().get(0));
        Assert.assertEquals("Crinj Tri-Tach Employee #495", movie.getActors().get(1));
        Assert.assertEquals("Hegemony Hive Scum :smile:", movie.getActors().get(2));
        Assert.assertEquals(2, movie.getDirectors().size()); //Should only be 1 director loaded in
        Assert.assertEquals("Some guy; don't worry about it", movie.getDirectors().get(0));
        Assert.assertEquals("[redacted]", movie.getDirectors().get(1));
        Assert.assertEquals(4, movie.getCategories().size()); //Should only have 4 categories
        Assert.assertEquals("In-Store Location 1", movie.getCategories().get(0));
        Assert.assertEquals("Crinj Compilation", movie.getCategories().get(1));
        Assert.assertEquals("Cringe Compilation", movie.getCategories().get(2));
        Assert.assertEquals("\"Jerry, why do we have this?\"", movie.getCategories().get(3));
    }

    @Test
    public void test_invalid_movie_search() {
        Movie movie = movies.getMovie(-600);
        Assert.assertNull(movie); //Non-existent movies return null
    }

    @Test
    public void test_add_existing_movie() {
        Movie movie = movies.getMovie(-1);
        boolean failAdd = movies.addMovie(movie);
        Assert.assertFalse(failAdd);

        Movie existMovie = new Movie();
        existMovie.setId(-1);
        failAdd = movies.addMovie(existMovie);
        Assert.assertFalse(failAdd);

        //Ensuring nothing was changed as we did this
        Movie movieInDB = movies.getMovie(-1);
        Assert.assertEquals(movie, movieInDB);
    }

    @Test
    public void test_remove_nonexisting_movie() {
        boolean dneMovie = movies.removeMovie(-600);
        Assert.assertFalse(dneMovie);
    }

    @Test
    public void test_add_remove_movie() {
        //See if the movie added successfully
        boolean movieAdded = movies.addMovie(newMovie);
        Assert.assertTrue(movieAdded);

        //Double checking if the movie added successfully
        Movie addedMovie = movies.getMovie(-495);
        Assert.assertEquals(newMovie.getId(), addedMovie.getId());
        Assert.assertEquals(newMovie.getStock(), addedMovie.getStock());
        Assert.assertEquals(newMovie.getPrice(), addedMovie.getPrice(), 0);
        Assert.assertEquals(newMovie.getTitle(), addedMovie.getTitle());
        Assert.assertEquals(newMovie.getReleaseDate(), addedMovie.getReleaseDate());
        Assert.assertEquals(newMovie.getGenre(), addedMovie.getGenre());
        Assert.assertEquals(newMovie.getDescription(), addedMovie.getDescription());
        for (int i = 0; i < newMovie.getActors().size(); i++)
            Assert.assertEquals(newMovie.getActors().get(i), addedMovie.getActors().get(i));
        for (int i = 0; i < newMovie.getDirectors().size(); i++)
            Assert.assertEquals(newMovie.getDirectors().get(i), addedMovie.getDirectors().get(i));
        for (int i = 0; i < newMovie.getCategories().size(); i++)
            Assert.assertEquals(newMovie.getCategories().get(i), addedMovie.getCategories().get(i));

        //Triple-check: Print to manually confirm movie added correctly
        System.out.println(addedMovie);

        //Removing the movie and seeing if it's removed successfully
        boolean movieRemoved = movies.removeMovie(-495);
        Assert.assertTrue(movieRemoved);

        //Double checking to see if the movie was removed from the DB
        Movie delMovie = movies.getMovie(-495);
        Assert.assertNull(delMovie);
    }

    @Test
    public void test_get_movie_by_title() {
        ArrayList<Movie> matches = movies.getMovie("[[Original Movie Name]]", 0);

        //Should be two matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-2 and #-3
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negTwo, matches.get(0));
        Assert.assertEquals(negThree, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY TITLE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_release_date() {
        ArrayList<Movie> matches = movies.getMovie("2020-04-20", 1);

        //Should be three matches with the original DB
        Assert.assertEquals(3, matches.size());

        //We get #-1, #-2 and #-3
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));
        Assert.assertEquals(negThree, matches.get(2));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
        System.out.println(matches.get(2).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_genre() {
        ArrayList<Movie> matches = movies.getMovie("TESTING", 2);

        //Should be three matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-1 and #-2
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_description() {
        //NB: "Crinj" in the actual description is all lowercase!  Doubles as a test to check case-insensitivity
        ArrayList<Movie> matches = movies.getMovie("Crinj", 3);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_full_description() {
        ArrayList<Movie> matches = movies.getMovie("Movie about the crinj Tri-Tachyon Corporation.", 3);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_actors() {
        ArrayList<Movie> matches = movies.getMovie("Crinj Tri-Tach Employee #495; Gengetsu", 4);

        //Should be three matches with the original DB
        Assert.assertEquals(3, matches.size());

        //We get #-1, #-2 and #-3
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));
        Assert.assertEquals(negThree, matches.get(2));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
        System.out.println(matches.get(2).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_directors() {
        ArrayList<Movie> matches = movies.getMovie("Shinki", 5);

        //Should be two matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-2 and #-3
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negTwo, matches.get(0));
        Assert.assertEquals(negThree, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY TITLE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_categories() {
        ArrayList<Movie> matches = movies.getMovie("In-Store Location 1", 6);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_modify_movie_properties() {
        //See if the movie added successfully
        boolean movieAdded = movies.addMovie(newMovie);
        Assert.assertTrue(movieAdded);

        //Double checking if the movie added successfully
        Movie addedMovie = movies.getMovie(-495);
        Assert.assertEquals(newMovie.getId(), addedMovie.getId());
        Assert.assertEquals(newMovie.getStock(), addedMovie.getStock());
        Assert.assertEquals(newMovie.getPrice(), addedMovie.getPrice(), 0);
        Assert.assertEquals(newMovie.getTitle(), addedMovie.getTitle());
        Assert.assertEquals(newMovie.getReleaseDate(), addedMovie.getReleaseDate());
        Assert.assertEquals(newMovie.getGenre(), addedMovie.getGenre());
        Assert.assertEquals(newMovie.getDescription(), addedMovie.getDescription());
        for (int i = 0; i < newMovie.getActors().size(); i++)
            Assert.assertEquals(newMovie.getActors().get(i), addedMovie.getActors().get(i));
        for (int i = 0; i < newMovie.getDirectors().size(); i++)
            Assert.assertEquals(newMovie.getDirectors().get(i), addedMovie.getDirectors().get(i));
        for (int i = 0; i < newMovie.getCategories().size(); i++)
            Assert.assertEquals(newMovie.getCategories().get(i), addedMovie.getCategories().get(i));

        //Modifying the movie via MovieDB
        int oldStock = movies.getMovie(-495).getStock();
        int stockSet = movies.setStock(-495, oldStock - 1);
        Assert.assertEquals(0, stockSet);
        Assert.assertEquals(oldStock - 1, movies.getMovie(-495).getStock()); //Checking stock #1
        Assert.assertEquals(oldStock - 1, addedMovie.getStock()); //Checking stock #2

        int priceSet = movies.setPrice(-495, 14.86);
        Assert.assertEquals(0, priceSet);
        Assert.assertEquals(14.86, movies.getMovie(-495).getPrice(), 0); //Check price #1
        Assert.assertEquals(14.86, addedMovie.getPrice(), 0); //Check price #1

        int titleSet = movies.setTRGD(-495, 0, "It's Time to Duel!");
        Assert.assertEquals(0, titleSet);

        int releaseDateSet = movies.setTRGD(-495, 1, "2077-12-31");
        Assert.assertEquals(0, releaseDateSet);

        int genreSet = movies.setTRGD(-495, 2, "Chain Nibiru.");
        Assert.assertEquals(0, genreSet);

        int descSet = movies.setTRGD(-495, 3, "The most powerful end board known to man.  Nib token, pass.");
        Assert.assertEquals(0, descSet);

        int actorsAdd = movies.setADC(-495, 0, movies.getMovie(-495).getActors().size(), "Chef"); //Adding an actor
        Assert.assertEquals(0, actorsAdd);

        int actorsSet = movies.setADC(-495, 0, 0, "Brique Wahl"); //modifying an actor
        Assert.assertEquals(0, actorsSet);

        int directorsAdd = movies.setADC(-495, 1, movies.getMovie(-495).getDirectors().size(), "Bob Bobert");
        Assert.assertEquals(0, directorsAdd);

        int directorsSet = movies.setADC(-495, 1, 0, "hackerman");
        Assert.assertEquals(0, directorsSet);

        int categoriesAdd = movies.setADC(-495, 2, movies.getMovie(-495).getCategories().size(), "I really don't know");
        Assert.assertEquals(0, categoriesAdd);

        int categoriesSet = movies.setADC(-495, 2, 0, "extreme_hand_holding");
        Assert.assertEquals(0, categoriesSet);

        //Double check: Print to manually confirm movie properties were changed
        System.out.println(addedMovie);

        //Removing the movie and seeing if it's removed successfully
        boolean movieRemoved = movies.removeMovie(-495);
        Assert.assertTrue(movieRemoved);

        //Double checking to see if the movie was removed from the DB
        Movie delMovie = movies.getMovie(-495);
        Assert.assertNull(delMovie);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_stock_movie() {
        int test = movies.setStock(-600, 100); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setStock(-1, movies.getMovie(-1).getStock()); //same price
        Assert.assertEquals(2, test);

        movies.setStock(-1, -1);
        Assert.fail("IllegalArgumentException should be thrown; stock can't be negative.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_price_movie() {
        int test = movies.setPrice(-600, 100); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setPrice(-1, movies.getMovie(-1).getPrice()); //same price
        Assert.assertEquals(2, test);

        movies.setPrice(-1, -1);
        Assert.fail("IllegalArgumentException should be thrown; price can't be negative.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_TRGD_input() {
        //NB: Setting the title, release date, genre and description all use the same method; no need
        //    for excessive testing here.
        int test = movies.setTRGD(-600, 0, "bruh"); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setTRGD(-1, 1, movies.getMovie(-1).getReleaseDate()); //Similar String
        Assert.assertEquals(2, test);

        test = movies.setTRGD(-1, 1, "[RANDOM NOISE]2021-11-01[SOME RANDOM TRASH]"); //Invalid date
        Assert.assertEquals(3, test);

        movies.setTRGD(-1, 2, ""); //Empty string
        Assert.fail("Should throw IllegalArgumentException on getting an empty string or a null string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_TRGD_flag() {
        movies.setTRGD(-1, 1231234, "spaghetti");
        Assert.fail("Should throw IllegalArgumentException when flag is not 0-3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_input() {
        //NB: Setting actors, directors and categories use the same method; can avoid excessive testing
        int test = movies.setADC(-600, 0, 0, "REISUB"); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setADC(-1, 1, 0, movies.getMovie(-1).getDirectors().get(0)); //Existing input
        Assert.assertEquals(2, test);

        movies.setADC(-1, 2, 0, null);
        Assert.fail("Should throw IllegalArgumentException on getting an empty string or a null string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_flag() {
        movies.setADC(-1, 123138, 0, "asofjiasgl");
        Assert.fail("Should throw IllegalArgumentException when flag is not 0-2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_propNum() {
        movies.setADC(-1, 0, movies.getMovie(-1).getActors().size() + 1, "Random person");
        Assert.fail("Should throw IllegalArgumentException when the propNum exceeds 1 more than the size of the property list.");
    }
}
