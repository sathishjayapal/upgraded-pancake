package me.sathish.myreads.utility;

import me.sathish.myreads.author.Author;
import me.sathish.myreads.author.AuthorRepo;
import me.sathish.myreads.books.Book;
import me.sathish.myreads.books.BookRepo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DataLoadUtil {

    public void initAuthors(String dataLocation, AuthorRepo authorRepo) {
        Path path = Paths.get(dataLocation);
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    Author author = new Author();
                    author.setName(jsonObject.optString("name"));
                    author.setPersonalName(jsonObject.optString("personal_name"));
                    author.setId(jsonObject.optString("key").replaceAll("/authors/", ""));
                    authorRepo.save(author);
                    System.out.println("Saving record for author " + author.getName());
                } catch (JSONException e) {
                    System.err.println("Skipping record for jsonString " + e.getLocalizedMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Skipping author record process" + e.getLocalizedMessage());
        }
    }

    public void initBooks(String dataLocation, BookRepo bookRepo, AuthorRepo authorRepo) {
        Path path = Paths.get(dataLocation);
        DateTimeFormatter dateTimeFormatter= DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS");
        try (Stream<String> lines = Files.lines(path)) {
            lines.forEach(line -> {
                String jsonString = line.substring(line.indexOf("{"));
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(jsonString);
                    Book book = new Book();
                    book.setId(jsonObject.getString("key").replace("/works/", ""));
                    book.setName(jsonObject.optString("title"));
                    JSONObject descrObject = jsonObject.optJSONObject("description");
                    if (descrObject != null)
                        book.setDescription(descrObject.optString("value"));
                    JSONObject publishedObj = jsonObject.optJSONObject("created");
                    if (publishedObj != null) {
                        String dateStr = publishedObj.getString("value");
                        book.setPublishedDate(LocalDate.parse(dateStr,dateTimeFormatter));
                    }
                    JSONArray coversJSONArr = jsonObject.optJSONArray("covers");
                    if (coversJSONArr != null) {
                        List<String> coverIds = new ArrayList<>();
                        for (int i = 0; i < coversJSONArr.length(); i++) {
                            coverIds.add(coversJSONArr.getString(i));
                        }
                    }
                    JSONArray authorsJSONArr = jsonObject.optJSONArray("authors");
                    if (authorsJSONArr != null) {
                        List<String> authorIDs = new ArrayList<>();
                        for (int i = 0; i < authorsJSONArr.length(); i++) {
                            String authorId = authorsJSONArr.getJSONObject(i).getJSONObject("author").getString("key").replace("/authors/", "");
                            authorIDs.add(authorId);
                        }
                        book.setAuthorId(authorIDs);
                        List<String> authorNames= authorIDs.stream().map(id->authorRepo.findById(id)).map(optionalAuthor->{
                            if(!optionalAuthor.isPresent())return "NA";
                            return optionalAuthor.get().getName();
                        }).collect(Collectors.toList());
                        book.setAuthorNames(authorNames);
                    }
                    bookRepo.save(book);
                } catch (JSONException jsonException) {
                    System.err.println("Skipping record for Book JSONString " + jsonException.getLocalizedMessage());
                } catch(Exception e){
                    System.err.println("Skipping record for Book JSONString " + e.getLocalizedMessage());
                }
            });
        } catch (IOException ios) {
            System.err.println("Skipping Book record process" + ios.getLocalizedMessage());
        }
    }
}
