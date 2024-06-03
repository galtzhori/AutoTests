import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class Weather {
    private static final String API_KEY = "a6919dd2e20c5ca67bcf1c727a0b36bf";
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    private static final String TEL_AVIV = "Tel-Aviv";
    private static final String LONDON = "London";
    private static final String NEW_YORK = "New York";

    @Test
    public void testWeatherAPI() {
        // For Tel-Aviv, expected country is "IL"
        checkWeather(TEL_AVIV, "IL", true);
        // For London, expected country is "GB"
        checkWeather(LONDON, "GB", false);
        // For New York, expected country is "US"
        checkWeather(NEW_YORK, "US", false);
    }

    private void checkWeather(String city, String expectedCountry, boolean isCelsius) {
        Response response = getWeatherResponse(city);

        // 1. Print the response data
        System.out.println("Response Data for " + city + ": " + response.getBody().asString());

        // 2. Print the response code
        int statusCode = response.getStatusCode();
        System.out.println("Response Code for " + city + ": " + statusCode);

        // 3. Verify that response code is equal to 200
        Assertions.assertEquals(200, statusCode, "Response code is not 200 for " + city);

        // Parse JSON response
        JSONObject jsonObject = new JSONObject(response.getBody().asString());

        // 4. Get the country
        String country = jsonObject.getJSONObject("sys").getString("country");
        System.out.println("Country for " + city + ": " + country);

        // 5. Get the temperature
        double tempKelvin = jsonObject.getJSONObject("main").getDouble("temp");
        if (isCelsius) {
            double tempCelsius = kelvinToCelsius(tempKelvin);
            System.out.println("Temperature in " + city + " (Celsius): " + tempCelsius);
        } else {
            double tempFahrenheit = kelvinToFahrenheit(tempKelvin);
            System.out.println("Temperature in " + city + " (Fahrenheit): " + tempFahrenheit);
        }

        // 6. Verify that country = expectedCountry
        Assertions.assertEquals(expectedCountry, country, "Country is not " + expectedCountry + " for " + city);
    }

    private Response getWeatherResponse(String city) {
        try {
            return RestAssured
                    .given()
                    .queryParam("q", city)
                    .queryParam("APPID", API_KEY)
                    .get(BASE_URL);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get weather data for " + city, e);
        }
    }

    private double kelvinToCelsius(double kelvin) {
        return kelvin - 273.15;
    }

    private double kelvinToFahrenheit(double kelvin) {
        return (kelvin - 273.15) * 9 / 5 + 32;
    }
}