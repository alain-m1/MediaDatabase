import java.util.ArrayList;

public class MediaRequest {
    private String title;
    private int year;
    private String description;
    private String mediaType;
    private String director;
    private String mpa = null;
    private ArrayList<String> genres = new ArrayList<>();

    public MediaRequest(String title, int year, String description, String mediaType, String director, String mpa) {
        this.title = title;
        this.year = year;
        this.description = description;
        this.mediaType = mediaType;
        this.director = director;
        if (this.mediaType.equals("M")) {
            this.mpa = mpa;
        }
    }

    public void addGenre(String genre) {
        if (!genres.contains(genre)) {
            genres.add(genre);
        }else{
            System.out.println("You already added " + genre);
        }
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getMediaType() {
        return mediaType;
    }

    public String getDescription() {
        return description;
    }

    public String getMpa() {
        return mpa;
    }

    public ArrayList<String> getGenres() {
        return genres;
    }

    @Override
    public String toString(){
        System.out.print("Title: " + title);
        System.out.println("Year: " + year);
        System.out.println("Description: " + description);
        System.out.println("MediaType: " + mediaType);
        System.out.println("Director: " + director);
        System.out.println("MPA: " + mpa);
        System.out.println("Genres: " + genres);

        return null;
    }
}
