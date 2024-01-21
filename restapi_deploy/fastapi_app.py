from fastapi import FastAPI, HTTPException
from fastapi.responses import JSONResponse
import socket
import datetime
import os
import requests

app = FastAPI()

@app.get("/health")
def health_check():
    return {"status": "ok"}

@app.get("/weather")
async def get_weather_data():
    api_key = os.environ.get('API_KEY')
    version = 2.5
    city = "Dhaka"
    unit_name = "metric"
    host_name = socket.gethostname()
    current_time = datetime.datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    api_url = f"http://api.openweathermap.org/data/{version}/weather"
    params = {
        "q": city,
        "units": unit_name,
        "appid": api_key
    }
    response = requests.get(api_url, params=params)
    data = response.json()
    parameter_data = {
        "hostname": host_name,
        "datetime": current_time,
        "version": version,
        "weather": {
            city: {
                "temperature": data["main"]["temp"],
                "temp_unit": unit_name
            }
        }
    }
    print(parameter_data)
    return parameter_data


if __name__ == '__main__':
    import uvicorn
    uvicorn.run(app, host='127.0.0.1', port=8000)
