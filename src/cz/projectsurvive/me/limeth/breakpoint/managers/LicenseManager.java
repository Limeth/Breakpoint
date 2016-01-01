package cz.projectsurvive.me.limeth.breakpoint.managers;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.bukkit.Bukkit;

public class LicenseManager
{
	public static boolean isAllowed()
	{
		try
		{
			String response = requestPost("http://plugin.projectsurvive.cz/isAllowed.php", "port=" + Bukkit.getPort());
			
			return response.equals("ALLOWED");
		}
		catch(IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	public static String requestPost(String url, String urlParameters) throws IOException
	{
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "BreakpointCheck");
		con.setRequestProperty("Accept-Language", "cs-CZ,cs;q=0.5");
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		
		while((inputLine = in.readLine()) != null)
			response.append(inputLine);
		
		in.close();
		
		String msg = response.toString();
		
		System.out.println("[Breakpoint] Response message: " + msg);
		
		return msg;
	}
}
