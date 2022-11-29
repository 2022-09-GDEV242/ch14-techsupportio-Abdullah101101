import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.*;
import java.util.*;

/**
 * The responder class represents a response generator object.
 * It is used to generate an automatic response, based on specified input.
 * Input is presented to the responder as a set of words. the responder will pull and generate
 * a response from hashResponse.txt where all the information of technical issues are stored.
 * 
 * 
 * If any input words are found responder class will generate a corresponding string
 * from the txt file (hasResponse.txt) to help the user with thier needs.
 * 
 * @author Abdullah Abdulwahab
 * @version 11.27.2022
 */
public class Responder
{
    // Used to map key words to responses.
    private HashMap<String, String> responseMap;
    // Default responses to use if we don't recognise a word.
    private ArrayList<String> defaultResponses;
    // The name of the file containing the default responses.
    private static final String FILE_OF_DEFAULT_RESPONSES = "default.txt";
    // file contating the hash map responses 
    private static final String FILE_OF_HASH_MAP_RESPONSES = "hashResponse.txt";
    private Random randomGenerator;

    /**
     * Construct a Responder
     */
    public Responder()
    {
        responseMap = new HashMap<>();
        defaultResponses = new ArrayList<>();
        fillResponseMap();
        fillDefaultResponses();
        randomGenerator = new Random();
    }

    /**
     * Generate a response from a given set of input words.
     * 
     * @param words  A set of words entered by the user
     * @return       A string that should be displayed as the response
     */
    public String generateResponse(HashSet<String> words)
    {
        Iterator<String> it = words.iterator();
        while(it.hasNext()) {
            String word = it.next();
            String response = responseMap.get(word);
            if(response != null) {
                return response;
            }
        }
        // If we get here, none of the words from the input line was recognized.
        // In this case we pick one of our default responses (what we say when
        // we cannot think of anything else to say...)
        return pickDefaultResponse();
    }

    /**
     * Enter all the known keywords and their associated responses
     * into our response map.
     */
    private void fillResponseMap()
    {

        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_HASH_MAP_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) 
        {
            int num = 0;
            String lineReader = reader.readLine();
            String responses = "";
            boolean factor = false;
            String response = "";

            while(lineReader != null) 
            {
                if (lineReader.trim().isEmpty())
                {
                    String[] keys = response.split(", ");
                    for (int i = 0; i < keys.length; i++) 
                    {
                        responseMap.put(keys[i], responses);
                    }
                    num = -1;

                }
                  if (num == 0)
                {
                    response = lineReader; 
                }
                else if(num >= 1)
                {
                    if (factor)
                    {
                        responses += "\n" + lineReader;
                    }   
                    
                }
    
                else
                {
                    responses = lineReader;
                }
                factor = true;
                lineReader = reader.readLine();
                num++;
            }
        }
        catch(FileNotFoundException e) 
        {
            System.err.println("Error has occured" + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) 
        {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_DEFAULT_RESPONSES);
        }
 
    }

    /**
     * Build up a list of default responses from which we can pick
     * if we don't know what else to say.
     */
    private void fillDefaultResponses()
    {
        Charset charset = Charset.forName("US-ASCII");
        Path path = Paths.get(FILE_OF_DEFAULT_RESPONSES);
        try (BufferedReader reader = Files.newBufferedReader(path, charset)) 
        {
            String nextLine = ""; 
            String responses = "";
            String lineReader = reader.readLine();
            while(lineReader != null && nextLine != null) 
            {
                nextLine = reader.readLine();
                if(nextLine != null){
                    if(nextLine.trim().length() != 0)
                    {
                        responses += nextLine;
                    }
                    else
                    {
                        if(responses != "")
                        {
                            defaultResponses.add(lineReader);
                            lineReader = reader.readLine();
                        }
                    }
                }
                lineReader = reader.readLine();
                if(lineReader != null)
                {
                    if(lineReader.trim().length() != 0)
                    {
                        responses += lineReader;
                    }
                    else
                    {
                        if(responses != "")
                        {
                            defaultResponses.add(responses);
                            responses = "";
                        }
                    }
                }
            }
            defaultResponses.add(responses);
        }
        catch(FileNotFoundException e) {
            System.err.println("Unable to open " + FILE_OF_DEFAULT_RESPONSES);
        }
        catch(IOException e) {
            System.err.println("A problem was encountered reading " +
                               FILE_OF_DEFAULT_RESPONSES);
        }
        // Make sure we have at least one response.
        if(defaultResponses.size() == 0) {
            defaultResponses.add("Could you elaborate on that?");
        }
    }

    /**
     * Randomly select and return one of the default responses.
     * @return     A random default response
     */
    private String pickDefaultResponse()
    {
        // Pick a random number for the index in the default response list.
        // The number will be between 0 (inclusive) and the size of the list (exclusive).
        int index = randomGenerator.nextInt(defaultResponses.size());
        return defaultResponses.get(index);
    }
}
