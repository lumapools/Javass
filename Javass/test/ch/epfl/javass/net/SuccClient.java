package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;

public final class SuccClient {
  public static void main(String[] args) {
    try (Socket s = new Socket("localhost", 5108);
	 BufferedReader r =
	   new BufferedReader(
	     new InputStreamReader(s.getInputStream(),
				   US_ASCII));
	 BufferedWriter w =
	   new BufferedWriter(
	     new OutputStreamWriter(s.getOutputStream(),
				    US_ASCII))) {
      int i = 2019;
      w.write(String.valueOf(i));
      w.write('\n');
      w.flush();
      int succ = Integer.parseInt(r.readLine());
      System.out.printf("succ(%d) = %d%n", i, succ);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}