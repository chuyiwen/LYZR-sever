package newbee.morningGlory.http.servlets.services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class AbstractService {

	public abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

}
