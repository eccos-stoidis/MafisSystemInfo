        function populateModalData(element) {
            const anlageNr = element.getAttribute('data-anlagen-nr');

            // Populate modal fields
             document.getElementById('anlageNr').value = anlageNr || '';



        }

      function submitEditForm(event) {
       // Prevent the default form submission
    event.preventDefault();
    // Get the Betreiber Name dropdown
    const betreiberNameDropdown = document.getElementById('betreiberName');

    // Get the selected value
    const selectedValue = betreiberNameDropdown.value;

    // Check if the value is empty
    if (!selectedValue.trim()) {
        // Highlight the dropdown and display an error message
        betreiberNameDropdown.classList.add('is-invalid'); // Add Bootstrap's invalid class

        // Add error message if not already present
        let errorFeedback = betreiberNameDropdown.nextElementSibling;
        if (!errorFeedback || !errorFeedback.classList.contains('invalid-feedback')) {
            errorFeedback = document.createElement('div');
            errorFeedback.classList.add('invalid-feedback');
            errorFeedback.textContent = "Bitte wählen Sie einen Betreiber aus";
            betreiberNameDropdown.parentNode.appendChild(errorFeedback); // Append error message
        }

        // Stop the form submission
        return;
    }

    // Remove any existing validation errors
    betreiberNameDropdown.classList.remove('is-invalid');
    if (betreiberNameDropdown.nextElementSibling) {
        betreiberNameDropdown.nextElementSibling.remove(); // Remove error message
    }

    // Submit the form if valid
    const form = document.getElementById('bearbeitenForm');
    form.submit();
}