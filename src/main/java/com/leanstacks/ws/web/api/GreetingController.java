package com.leanstacks.ws.web.api;

import java.util.Collection;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.leanstacks.ws.model.Greeting;
import com.leanstacks.ws.service.EmailService;
import com.leanstacks.ws.service.GreetingService;

/**
 * The GreetingController class is a RESTful web service controller. The <code>@RestController</code> annotation informs
 * Spring that each <code>@RequestMapping</code> method returns a <code>@ResponseBody</code>.
 * 
 * @author Matt Warman
 */
@RestController
public class GreetingController extends BaseController {

    /**
     * The GreetingService business service.
     */
    @Autowired
    private GreetingService greetingService;

    /**
     * The EmailService business service.
     */
    @Autowired
    private EmailService emailService;

    /**
     * Web service endpoint to fetch all Greeting entities. The service returns the collection of Greeting entities as
     * JSON.
     * 
     * @return A ResponseEntity containing a Collection of Greeting objects.
     * @throws Exception Thrown if a problem occurs completing the request.
     */
    @RequestMapping(value = "/api/greetings",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Collection<Greeting>> getGreetings() {
        logger.info("> getGreetings");

        Collection<Greeting> greetings = greetingService.findAll();

        logger.info("< getGreetings");
        return new ResponseEntity<Collection<Greeting>>(greetings, HttpStatus.OK);
    }

    /**
     * <p>
     * Web service endpoint to fetch a single Greeting entity by primary key identifier.
     * </p>
     * <p>
     * If found, the Greeting is returned as JSON with HTTP status 200. If not found, the service returns an empty
     * response body with HTTP status 404.
     * </p>
     * 
     * @param id A Long URL path variable containing the Greeting primary key identifier.
     * @return A ResponseEntity containing a single Greeting object, if found, and a HTTP status code as described in
     *         the method comment.
     * @throws Exception Thrown if a problem occurs completing the request.
     */
    @RequestMapping(value = "/api/greetings/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> getGreeting(@PathVariable Long id) {
        logger.info("> getGreeting");

        Greeting greeting = greetingService.findOne(id);
        if (greeting == null) {
            logger.info("< getGreeting");
            return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
        }

        logger.info("< getGreeting");
        return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);
    }

    /**
     * <p>
     * Web service endpoint to create a single Greeting entity. The HTTP request body is expected to contain a Greeting
     * object in JSON format. The Greeting is persisted in the data repository.
     * </p>
     * <p>
     * If created successfully, the persisted Greeting is returned as JSON with HTTP status 201. If not created
     * successfully, the service returns an empty response body with HTTP status 500.
     * </p>
     * 
     * @param greeting The Greeting object to be created.
     * @return A ResponseEntity containing a single Greeting object, if created successfully, and a HTTP status code as
     *         described in the method comment.
     * @throws Exception Thrown if a problem occurs completing the request.
     */
    @RequestMapping(value = "/api/greetings",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> createGreeting(@RequestBody Greeting greeting) {
        logger.info("> createGreeting");

        Greeting savedGreeting = greetingService.create(greeting);

        logger.info("< createGreeting");
        return new ResponseEntity<Greeting>(savedGreeting, HttpStatus.CREATED);
    }

    /**
     * <p>
     * Web service endpoint to update a single Greeting entity. The HTTP request body is expected to contain a Greeting
     * object in JSON format. The Greeting is updated in the data repository.
     * </p>
     * <p>
     * If updated successfully, the persisted Greeting is returned as JSON with HTTP status 200. If not found, the
     * service returns an empty response body and HTTP status 404. If not updated successfully, the service returns an
     * empty response body with HTTP status 500.
     * </p>
     * 
     * @param greeting The Greeting object to be updated.
     * @return A ResponseEntity containing a single Greeting object, if updated successfully, and a HTTP status code as
     *         described in the method comment.
     * @throws Exception Thrown if a problem occurs completing the request.
     */
    @RequestMapping(value = "/api/greetings/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> updateGreeting(@RequestBody Greeting greeting) {
        logger.info("> updateGreeting");

        Greeting updatedGreeting = greetingService.update(greeting);

        logger.info("< updateGreeting");
        return new ResponseEntity<Greeting>(updatedGreeting, HttpStatus.OK);
    }

    /**
     * <p>
     * Web service endpoint to delete a single Greeting entity. The HTTP request body is empty. The primary key
     * identifier of the Greeting to be deleted is supplied in the URL as a path variable.
     * </p>
     * <p>
     * If deleted successfully, the service returns an empty response body with HTTP status 204. If not deleted
     * successfully, the service returns an empty response body with HTTP status 500.
     * </p>
     * 
     * @param id A Long URL path variable containing the Greeting primary key identifier.
     * @return A ResponseEntity with an empty response body and a HTTP status code as described in the method comment.
     * @throws Exception Throw if a problem occurs completing the request.
     */
    @RequestMapping(value = "/api/greetings/{id}",
            method = RequestMethod.DELETE)
    public ResponseEntity<Greeting> deleteGreeting(@PathVariable("id") Long id) throws Exception {
        logger.info("> deleteGreeting");

        greetingService.delete(id);

        logger.info("< deleteGreeting");
        return new ResponseEntity<Greeting>(HttpStatus.NO_CONTENT);
    }

    /**
     * <p>
     * Web service endpoint to fetch a single Greeting entity by primary key identifier and send it as an email.
     * </p>
     * <p>
     * If found, the Greeting is returned as JSON with HTTP status 200 and sent via Email. If not found, the service
     * returns an empty response body with HTTP status 404.
     * </p>
     * 
     * @param id A Long URL path variable containing the Greeting primary key identifier.
     * @param waitForAsyncResult A boolean indicating if the web service should wait for the asynchronous email
     *            transmission.
     * @return A ResponseEntity containing a single Greeting object, if found, and a HTTP status code as described in
     *         the method comment.
     */
    @RequestMapping(value = "/api/greetings/{id}/send",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Greeting> sendGreeting(@PathVariable("id") Long id, @RequestParam(value = "wait",
            defaultValue = "false") boolean waitForAsyncResult) {

        logger.info("> sendGreeting");

        Greeting greeting = null;

        try {
            greeting = greetingService.findOne(id);
            if (greeting == null) {
                logger.info("< sendGreeting");
                return new ResponseEntity<Greeting>(HttpStatus.NOT_FOUND);
            }

            if (waitForAsyncResult) {
                Future<Boolean> asyncResponse = emailService.sendAsyncWithResult(greeting);
                boolean emailSent = asyncResponse.get();
                logger.info("- greeting email sent? {}", emailSent);
            } else {
                emailService.sendAsync(greeting);
            }
        } catch (Exception ex) {
            logger.error("A problem occurred sending the Greeting.", ex);
            return new ResponseEntity<Greeting>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        logger.info("< sendGreeting");
        return new ResponseEntity<Greeting>(greeting, HttpStatus.OK);

    }

}
