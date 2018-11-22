package com.github.kklisura.spring.spa.utils.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.kklisura.spring.spa.controllers.errors.Error;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

/**
 * Response utils handle any response related functions.
 *
 * @author Kenan Klisura
 */
public final class ResponseUtils {

  private static final ObjectWriter OBJECT_WRITER = new ObjectMapper()
      .registerModule(new JavaTimeModule())
      .writerFor(Error.class);

  private ResponseUtils() {
    // Private ctor.
  }

  /**
   * Send an error object as a response.
   *
   * @param error    Error.
   * @param response Response.
   */
  public static void sendError(Error error, HttpServletResponse response) {
    response.setStatus(error.getStatus().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

    try {
      OBJECT_WRITER.writeValue(response.getOutputStream(), error);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
