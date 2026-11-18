package com.dhilip.pickles;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping(value = "/", produces = "text/html")
    public String home() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Dhilip Pickles</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        text-align: center;
                        background: #fff8e7;
                        padding: 80px;
                    }
                    h1 { color: #a52a2a; }
                    p { font-size: 20px; }
                </style>
            </head>
            <body>
                <h1>Welcome to Dhilip Pickles</h1>
                <p>Authentic Homemade Pickles</p>
                <p>Fresh Taste | Quality | Tradition</p>
            </body>
            </html>
            """;
    }
}
