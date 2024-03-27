My app is a weather app named 4cast, which displays the current and future weather forecast for the current location of the user. When loaded up, it will need location permissions (it does ask) and will need internet connection as it uses an API.

The top part of the home page shows the current temperature, a brief description of the current weather, a logo for the current weather and the current location. When the current location is clicked it will use an implicit intent to open that location up in google maps.
 
The background of the UI is dependent on the current weather (for example if raining the background will be a rain style design, if cloudy it will be a cloud style design).

The bottom part of the home page is a RecyclerView that displays the weather forecast every three hours for the next 5 days. Each item contains a symbol describing the weather of that forecast, the day and time of that forecast, the temperature of that forecast and a brief description of the weather of that forecast.

There is a settings button in the top left which opens the settings page using explicit intent. This settings page allows the user to change between Celsius and Fahrenheit and uses SharedPreferences to save this setting even when the app is shut. The save button saves this and navigates back to the home page.

I used a relative view for my main page and my settings page as it allows the page to flexibly adjust itself depending on the screen size therefore allowing for a responsive design. I also used it for each individual item in the RecyclerView as it allowed me to base each item’s positioning off the other items in the view.

I used SharedPreferences to save the unit the user wanted the temperature displayed in because it is easy to use considering I am only saving a small amount of data.

I used an API to gather all the weather forecasting data and used a location request system to get the location of the user. These were the most complicated parts to implement. 

If the user does not have location permissions, it will ask for them. Once it gets these it will then save the latitude and longitude and use them in the api request. The API request uses these, and the units saved in SharedPreferences. It then gets a massive JSON file in response, which I filter through for the relative information and store each forecast as an object, then create a list of these objects. This list is then used for the Recycler View.

The main challenge I faced was how to effectively implement implicit intent into my app. In the end I did this by opening google maps and getting the current location up. This would be helped by an additional feature that I would like to implement in the future where you can search up specific locations and get the weather forecast there as well as your current location.

Another thing I may improve is the current weather forecast, as currently it just takes the closest one that the API gives and that can be up to three hours out, however to fix this I would need to pay for the API.
