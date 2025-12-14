// Mobile Menu (Simple toggle function)
function toggleMenu() {
  // Get the navigation element from the HTML by its ID
  const nav = document.getElementById("myTopnav");
  // Find the icon element inside the navigation
  const icon = nav.querySelector(".icon");

  // Check if the menu is currently closed (default class "topnav")
  if (nav.className === "topnav") {
    // Add the 'responsive' class to show the mobile menu
    nav.className += " responsive";
    icon.innerHTML = "&#10005;"; // Symbol X (Change icon to Close)
  } else {
    // Remove the 'responsive' class to hide the mobile menu
    nav.className = "topnav";
    icon.innerHTML = "&#9776;"; // Symbol Menu (Change icon back to Hamburger)
  }
}

// Load Content when page is ready (wait for HTML to fully load)
document.addEventListener("DOMContentLoaded", function () {
  // --- Total Points Page ---
  // We check if the element exists to know if we are on the right page
  const pointsElement = document.getElementById("total-points");

  if (pointsElement) {
    // Send a request to get the settings file
    fetch("json/settings.json")
      .then(function (response) {
        // Wait for server response and convert text to JSON object
        return response.json(); // Convert text to JSON object
      })
      .then(function (data) {
        // Use the data to update the HTML elements
        // Put the text directly into HTML using innerHTML
        document.getElementById("total-points").innerHTML =
          data.communityPoints;
        document.getElementById("goal-text").innerHTML = data.communityGoal;
        document.getElementById("target-text").innerHTML = data.targetPoints;
      })
      .catch(function (error) {
        // Log any errors (like file not found) to the console
        console.error("Error:", error);
      });
  }

  // --- Green Actions Page ---
  const greenContainer = document.getElementById("green-list");

  if (greenContainer) {
    // Fetch the list of green actions data
    fetch("json/green.json")
      .then(function (response) {
        return response.json();
      })
      .then(function (actions) {
        // Build a long string of HTML
        let text = "";

        // Loop through all actions
        for (let i = 0; i < actions.length; i++) {
          // We add (+) new HTML pieces to our text variable
          // Access data using array index [i] (e.g., actions[i].title)
          text += '<div class="card">';
          text += "  <div>";
          text += "    <h3>" + actions[i].title + "</h3>";
          text += "    <p>" + actions[i].description + "</p>";
          text += "  </div>";
          text +=
            '  <p class="price">+' +
            actions[i].pointValue +
            " Community Points</p>";
          text += "</div>";
        }

        // If the list is empty, show a message
        if (actions.length === 0) {
          text = "<p>No actions yet.</p>";
        }

        // Put the accumulated text string into the page
        greenContainer.innerHTML = text;
      })
      .catch(function (error) {
        console.error("Error:", error);
      });
  }

  // --- Communal Tasks Page ---
  const communalContainer = document.getElementById("communal-list");

  if (communalContainer) {
    // Fetch the tasks data
    fetch("json/communal.json")
      .then(function (response) {
        return response.json();
      })
      .then(function (tasks) {
        let text = "";

        // Loop through each task in the array
        for (let i = 0; i < tasks.length; i++) {
          // Build the HTML card string for this task
          text += '<div class="card">';
          text += "  <div>";
          text += "    <h3>" + tasks[i].title + "</h3>";
          text += "    <p>" + tasks[i].description + "</p>";
          text +=
            '    <p class="author">Deadline: ' + tasks[i].deadline + "</p>";
          text += "  </div>";
          text +=
            '  <p class="price">Earn ' +
            tasks[i].pointValue +
            " Personal Points</p>";
          text += "</div>";
        }

        if (tasks.length === 0) {
          text = "<p>No tasks available.</p>";
        }

        // Display the result inside the container
        communalContainer.innerHTML = text;
      });
  }

  // --- Trade Offers Page (Nested Fetch) ---
  const tradeContainer = document.getElementById("trade-list");

  if (tradeContainer) {
    // Get Members (to know names matching the IDs)
    fetch("json/members.json")
      .then(function (response) {
        return response.json();
      })
      .then(function (members) {
        // Get Trades (only after we have the members data)
        fetch("json/trade.json")
          .then(function (response) {
            return response.json();
          })
          .then(function (trades) {
            let text = "";

            // Loop through every trade offer
            for (let i = 0; i < trades.length; i++) {
              // Find author name using ID from the trade object
              const authorId = trades[i].performerID;
              let authorName = "Unknown";

              // Check if this ID exists in our members data object
              // Access object by key: members["some-id"]
              if (members[authorId]) {
                authorName = members[authorId].name;
              }

              // Add the trade card HTML to our text string
              text += '<div class="card">';
              text += "  <div>";
              text += "    <h3>" + trades[i].title + "</h3>";
              text += "    <p>" + trades[i].description + "</p>";
              text +=
                '    <p class="author">Offered by: <strong>' +
                authorName +
                "</strong></p>";
              text += "  </div>";
              text +=
                '  <p class="price">Cost: ' +
                trades[i].pointValue +
                " Personal Points</p>";
              text += "</div>";
            }

            if (trades.length === 0) {
              text = "<p>No offers available.</p>";
            }

            // Display everything on the page
            tradeContainer.innerHTML = text;
          });
      });
  }
});
