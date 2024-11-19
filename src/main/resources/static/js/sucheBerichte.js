 document.getElementById('searchType').addEventListener('change', function () {
            const additionalFilters = document.getElementById('additionalFilters');
            additionalFilters.innerHTML = ''; // Clear existing filters
            additionalFilters.classList.add('d-none');

            if (this.value === 'betriebe') {
                additionalFilters.innerHTML = `
                    <label for="betriebeInput" class="form-label">Betriebe Name</label>
                    <input type="text" id="betriebeInput" name="betriebe" class="form-control" placeholder="Geben Sie einen Betriebename ein">
                    <div class="invalid-feedback">Füllen Sie dieses Feld aus</div>

                `;

            } else if (this.value === 'profitcenter') {
                  additionalFilters.innerHTML = `
                   <label for="betriebeInput" class="form-label">Zugehöriger Profitcenter</label>
                   <input type="text" id="profitcenterInput" name="profitcenter" class="form-control"placeholder="Geben Sie einen Profitcenter ein">
                   <div class="invalid-feedback">Füllen Sie dieses Feld aus</div>
                 `;

            } else if (this.value === 'module') {
                 additionalFilters.innerHTML = `
                   <label for="moduleInput" class="form-label">Modul Typ</label>
                   <input type="text" id="moduleInput" name="module" class="form-control"placeholder="Geben Sie einen Modultyp ein">
                   <div class="invalid-feedback">Füllen Sie dieses Feld aus</div>
                 `;

            } else if (this.value === 'kassen') {
                 additionalFilters.innerHTML = `
                    <label for="kasseInput" class="form-label">Kasse Typ</label>
                    <div id="kasseOptions" class="kasse-options-grid">
                        <input type="checkbox" id="kasse_intern" value="Intern" name="kasseType">
                        <label for="kasse_intern">Intern</label><br>
                        <input type="checkbox" id="kasse_extern" value="Extern" name="kasseType">
                        <label for="kasse_extern">Extern</label><br>
                        <input type="checkbox" id="kasse_systeme" value="Système" name="kasseType">
                        <label for="kasse_systeme">Système</label><br>
                        <input type="checkbox" id="kasse_system" value="System" name="kasseType">
                        <label for="kasse_system">System</label><br>
                        <input type="checkbox" id="kasse_mobil" value="Mobil" name="kasseType">
                        <label for="kasse_mobil">Mobil</label><br>
                        <input type="checkbox" id="kasse_counter" value="Counter" name="kasseType">
                        <label for="kasse_counter">Counter</label>
                    </div>
                 `;
            }else if (this.value === 'schnittstellen') {
                     additionalFilters.innerHTML = `
                         <label for="typInput" class="form-label">Schnittstellen Typ</label>
                         <input type="text" id="typInput" name="typ" class="form-control" placeholder="Geben Sie einen Schnittstellen-Typ ein">

                         <label for="untertypInput" class="form-label mt-2">Untertyp</label>
                         <input type="text" id="untertypInput" name="untertyp" class="form-control" placeholder="Geben Sie einen Untertyp ein">
                     `;
            }else if (this.value === 'automaten') {
                     additionalFilters.innerHTML = `
                         <label for="engineVersionInput" class="form-label">Engine Version</label>
                         <input type="text" id="engineVersionInput" name="engineVersion" class="form-control" placeholder="Geben Sie eine Engine-Version ein">

                         <label for="fccVersionInput" class="form-label mt-2">FCC Version</label>
                         <input type="text" id="fccVersionInput" name="fccVersion" class="form-control" placeholder="Geben Sie eine FCC-Version ein">

                         <label for="typInput" class="form-label mt-2">Typ</label>
                         <input type="text" id="typInput" name="typ" class="form-control" placeholder="Geben Sie einen Typ ein">

                         <label for="unterTypInput" class="form-label mt-2">Untertyp</label>
                         <input type="text" id="unterTypInput" name="unterTyp" class="form-control" placeholder="Geben Sie einen Untertyp ein">
                     `;
            }else if (this.value === 'zutritts') {
                    additionalFilters.innerHTML = `
                           <label for="vonSektorInput" class="form-label">Von Sektor</label>
                           <input type="text" id="vonSektorInput" name="vonSektor" class="form-control" placeholder="Geben Sie einen Von-Sektor ein">

                           <label for="nachSektorInput" class="form-label mt-2">Nach Sektor</label>
                           <input type="text" id="nachSektorInput" name="nachSektor" class="form-control" placeholder="Geben Sie einen Nach-Sektor ein">
                       `;
            }else if (this.value === 'medienarten') {
                     additionalFilters.innerHTML = `
                            <label for="medienartenInput" class="form-label">Medienarten Typ</label>
                            <div id="medienartenOptions" class="kasse-options-grid">
                                <input type="checkbox" id="medienarten_hitac" value="Hitag" name="medienartenType">
                                <label for="medienarten_hitac">Hitag</label><br>

                                <input type="checkbox" id="medienarten_miware" value="Mifare" name="medienartenType">
                                <label for="medienarten_miware">Mifare</label><br>

                                <input type="checkbox" id="medienarten_qr" value="QR" name="medienartenType">
                                <label for="medienarten_qr">QR</label><br>

                                <input type="checkbox" id="medienarten_barcode" value="Barcode" name="medienartenType">
                                <label for="medienarten_barcode">Barcode</label><br>
                            </div>
                        `;
           }
              else if (this.value === 'fiskaldaten') {
                     additionalFilters.innerHTML = `
                         <label for="typSelect" class="form-label">Typ</label>
                         <select id="typSelect" name="typ" class="form-select">
                             <option value="all">All</option>
                             <option value="efsta">Efsta</option>
                             <option value="fiskaltrust">Fiskaltrust</option>
                         </select>
                     `;
            }


            // Show the filter options if there are any
            if (additionalFilters.innerHTML !== '') {
                additionalFilters.classList.remove('d-none');
            }
        });

        function applySearch() {
            const searchType = document.getElementById('searchType').value;
            const filterData = {};

            if (searchType === 'betriebe') {
                   const betriebName = document.getElementById('betriebeInput').value;

                   // Check if the input is empty
                   if (!betriebName.trim()) {
                       // Display validation message
                       const betriebInput = document.getElementById('betriebeInput');
                       betriebInput.classList.add('is-invalid'); // Add Bootstrap's invalid class
                       betriebInput.nextElementSibling.textContent = "Füllen Sie dieses Feld aus"; // Set validation message
                       return; // Stop the function if the field is empty
                   } else {
                       // Remove validation styles if the field is filled
                       document.getElementById('betriebeInput').classList.remove('is-invalid');
                   }

                   filterData.betriebName = betriebName;
               }

              if (searchType === 'profitcenter') {
                     const profitcenterInput = document.getElementById('profitcenterInput');
                     const invalidFeedback = profitcenterInput.nextElementSibling;
                     const zugProfitcenter = profitcenterInput.value;

                     if (!zugProfitcenter.trim()) {
                         profitcenterInput.classList.add('is-invalid');
                         if (invalidFeedback) {
                             invalidFeedback.textContent = "Füllen Sie dieses Feld aus";
                         }
                         return;
                     } else {
                         profitcenterInput.classList.remove('is-invalid');
                         if (invalidFeedback) {
                             invalidFeedback.textContent = "";
                         }
                     }

                     filterData.zugProfitcenter = zugProfitcenter;
                 }
              if (searchType === 'module') {
                    const moduleInput = document.getElementById('moduleInput');
                    const invalidFeedback = moduleInput.nextElementSibling;
                    const modulTyp = moduleInput.value;

                    if (!modulTyp.trim()) {
                        moduleInput.classList.add('is-invalid');
                        if (invalidFeedback) {
                            invalidFeedback.textContent = "Füllen Sie dieses Feld aus";
                        }
                        return;
                    } else {
                        moduleInput.classList.remove('is-invalid');
                        if (invalidFeedback) {
                            invalidFeedback.textContent = "";
                        }
                    }

                    filterData.modulTyp = modulTyp;
                }
                if (searchType === 'kassen') {
                      // Get selected checkboxes
                      const selectedKasseTypes = Array.from(document.querySelectorAll('input[name="kasseType"]:checked'))
                          .map(checkbox => checkbox.value);

                      // Reference to the label for validation message
                      const kasseLabel = document.querySelector('label[for="kasseInput"]');

                      // Check if any checkbox is selected
                      if (selectedKasseTypes.length === 0) {

                          // Create a validation message if it doesn't already exist
                          let validationMessage = document.querySelector('.kasse-validation-message');
                          if (!validationMessage) {
                              validationMessage = document.createElement('div');
                              validationMessage.classList.add('kasse-validation-message', 'invalid-feedback', 'd-block');
                              validationMessage.textContent = "Bitte wählen Sie mindestens eine Option aus";
                              kasseLabel.before(validationMessage);
                          }
                          return; // Stop the function if no checkbox is selected
                      } else {
                          // Remove error styling and validation message if at least one checkbox is selected
                          kasseLabel.classList.remove('text-danger');
                          const existingMessage = document.querySelector('.kasse-validation-message');
                          if (existingMessage) {
                              existingMessage.remove();
                          }

                          // Add selected checkboxes to filterData
                          filterData.typ = selectedKasseTypes;
                      }
             } else if (searchType === 'schnittstellen') {
                        // Get values from input fields
                          const typ = document.getElementById('typInput').value.trim();
                          const untertyp = document.getElementById('untertypInput').value.trim();

                          // Reference to the label for validation message
                          const typLabel = document.querySelector('label[for="typInput"]');

                          // Check if all fields are empty
                          if (!typ && !untertyp) {
                              document.getElementById('typInput').classList.add('is-invalid');
                              document.getElementById('untertypInput').classList.add('is-invalid');

                              // Create a validation message if it doesn't already exist
                              let validationMessage = document.querySelector('.schnittstellen-validation-message');
                              if (!validationMessage) {
                                  validationMessage = document.createElement('div');
                                  validationMessage.classList.add('schnittstellen-validation-message', 'invalid-feedback', 'd-block');
                                  validationMessage.textContent = "Bitte geben Sie mindestens einen Wert ein";

                                  // Insert the validation message above the "Schnittstellen Typ" label
                                  typLabel.before(validationMessage);
                              }
                              return; // Stop the function if validation fails
                          } else {
                              // Remove error styling and validation message if at least one field is filled
                              document.getElementById('typInput').classList.remove('is-invalid');
                              document.getElementById('untertypInput').classList.remove('is-invalid');

                              const existingMessage = document.querySelector('.schnittstellen-validation-message');
                              if (existingMessage) {
                                  existingMessage.remove();
                              }

                              // Add values to filterData
                              filterData.typ = typ;
                              filterData.untertyp = untertyp;
                          }
             }else if (searchType === 'automaten') {
                       // Get values from input fields
                           const engineVersion = document.getElementById('engineVersionInput').value.trim();
                           const fccVersion = document.getElementById('fccVersionInput').value.trim();
                           const typ = document.getElementById('typInput').value.trim();
                           const unterTyp = document.getElementById('unterTypInput').value.trim();

                           // Reference to the label for validation message
                           const engineVersionLabel  = document.querySelector('label[for="engineVersionInput"]');

                           // Check if all fields are empty
                           if (!engineVersion && !fccVersion && !typ && !unterTyp) {
                               engineVersionInput.classList.add('is-invalid');
                               fccVersionInput.classList.add('is-invalid');
                               typInput.classList.add('is-invalid');
                               unterTypInput.classList.add('is-invalid');

                               // Create a validation message if it doesn't already exist
                               let validationMessage = document.querySelector('.automaten-validation-message');
                               if (!validationMessage) {
                                   validationMessage = document.createElement('div');
                                   validationMessage.classList.add('automaten-validation-message', 'invalid-feedback', 'd-block');
                                   validationMessage.textContent = "Bitte geben Sie mindestens einen Wert ein";
                                    // Insert the validation message above the "Engine Version" label
                                   engineVersionLabel.before(validationMessage);
                               }
                               return; // Stop the function if validation fails
                           } else {
                               // Remove error styling and validation message if at least one field is filled
                               const existingMessage = document.querySelector('.automaten-validation-message');
                               if (existingMessage) {
                                   existingMessage.remove();
                               }

                               // Add values to filterData
                               filterData.engineVersion = engineVersion;
                               filterData.fccVersion = fccVersion;
                               filterData.typ = typ;
                               filterData.unterTyp = unterTyp;
                           }
             }else if (searchType === 'zutritts') {
                       // Get values from input fields
                         const vonSektor = document.getElementById('vonSektorInput').value.trim();
                         const nachSektor = document.getElementById('nachSektorInput').value.trim();

                         // Reference to the label for validation message
                         const vonSektorLabel = document.querySelector('label[for="vonSektorInput"]');

                         // Check if all fields are empty
                         if (!vonSektor && !nachSektor) {
                             document.getElementById('vonSektorInput').classList.add('is-invalid');
                             document.getElementById('nachSektorInput').classList.add('is-invalid');

                             // Create a validation message if it doesn't already exist
                             let validationMessage = document.querySelector('.zutritts-validation-message');
                             if (!validationMessage) {
                                 validationMessage = document.createElement('div');
                                 validationMessage.classList.add('zutritts-validation-message', 'invalid-feedback', 'd-block');
                                 validationMessage.textContent = "Bitte geben Sie mindestens einen Wert ein";

                                 // Insert the validation message above the "Von Sektor" label
                                 vonSektorLabel.before(validationMessage);
                             }
                             return; // Stop the function if validation fails
                         } else {
                             // Remove error styling and validation message if at least one field is filled
                             document.getElementById('vonSektorInput').classList.remove('is-invalid');
                             document.getElementById('nachSektorInput').classList.remove('is-invalid');

                             const existingMessage = document.querySelector('.zutritts-validation-message');
                             if (existingMessage) {
                                 existingMessage.remove();
                             }

                             // Add values to filterData
                             filterData.vonSektor = vonSektor;
                             filterData.nachSektor = nachSektor;
                         }
             }else if (searchType === 'medienarten') {
                  // Get selected checkboxes
                  const selectedMedienartenTypes = Array.from(document.querySelectorAll('input[name="medienartenType"]:checked'))
                      .map(checkbox => checkbox.value);

                  // Reference to the label for validation message
                  const medienartenLabel = document.querySelector('label[for="medienartenInput"]');

                  // Check if any checkbox is selected
                  if (selectedMedienartenTypes.length === 0) {
                      // Create a validation message if it doesn't already exist
                      let validationMessage = document.querySelector('.medienarten-validation-message');
                      if (!validationMessage) {
                          validationMessage = document.createElement('div');
                          validationMessage.classList.add('medienarten-validation-message', 'invalid-feedback', 'd-block');
                          validationMessage.textContent = "Bitte wählen Sie mindestens eine Option aus";
                          medienartenLabel.before(validationMessage);
                      }
                      return; // Stop the function if no checkbox is selected
                  } else {
                      // Remove error styling and validation message if at least one checkbox is selected
                      medienartenLabel.classList.remove('text-danger');
                      const existingMessage = document.querySelector('.medienarten-validation-message');
                      if (existingMessage) {
                          existingMessage.remove();
                      }

                      // Add selected checkboxes to filterData
                      filterData.typ = selectedMedienartenTypes;
                  }
              }



             else  if (searchType === 'fiskaldaten') {
                      filterData.typ = document.getElementById('typSelect').value;
             }

            // Determine the URL based on search type
            let url = '';
            if (searchType === 'betriebe') {
               url = `/getAnlageByBetrieb?betriebName=${encodeURIComponent(filterData.betriebName || '')}`;
            } else if (searchType === 'profitcenter') {
               url = `/getAnlageByProfitCenter?zugProfitcenter=${encodeURIComponent(filterData.zugProfitcenter || '')}`;
            } else if (searchType === 'module') {
               url = `/getAnlageByModulTyp?modulTyp=${encodeURIComponent(filterData.modulTyp || '')}`;
            } else if (searchType === 'kassen' && filterData.typ.length) {
               url = `/getAnlageByKasse?typ=${encodeURIComponent(filterData.typ.join(','))}`;
            } else if (searchType === 'schnittstellen') {
               const typParam = filterData.typ ? `typ=${encodeURIComponent(filterData.typ)}` : '';
               const untertypParam = filterData.untertyp ? `untertyp=${encodeURIComponent(filterData.untertyp)}` : '';
               url = `/getAnlageBySchnittstelle?${[typParam, untertypParam].filter(Boolean).join('&')}`;
            } else if (searchType === 'automaten') {
               const params = new URLSearchParams({
                engineVersion: filterData.engineVersion || '',
                fccVersion: filterData.fccVersion || '',
                typ: filterData.typ || '',
                unterTyp: filterData.unterTyp || ''
               });
               url = `/getAnlageByAutomaten?${params.toString()}`;
           }else if (searchType === 'zutritts') {
                const params = new URLSearchParams({
                    vonSektor: filterData.vonSektor || '',
                    nachSektor: filterData.nachSektor || ''
                });
                url = `/getAnlageByZutritts?${params.toString()}`;
           }else if (searchType === 'medienarten') {
                // Define typ_nr mappings for each Medienarten type
                   const medienartenMapping = {
                       "Hitag": [4, 5, 6, 7, 8],
                       "Mifare": [30,31, 32, 33, 34],
                       "QR": [17],
                       "Barcode": [2, 3, 16]
                   };

                   // Collect selected Medienarten types
                   const selectedMedienartenTypes = Array.from(document.querySelectorAll('input[name="medienartenType"]:checked'))
                       .map(checkbox => medienartenMapping[checkbox.value])
                       .flat(); // Flatten to get a single array of typ_nr values

                   // Check if there are selected types and construct the URL
                   if (selectedMedienartenTypes.length) {
                       url = `/getAnlageByMedienarten?typ=${encodeURIComponent(selectedMedienartenTypes.join(','))}`;
                   }
           }

           else if (searchType === 'fiskaldaten') {
                    url = `/getAnlageByFiskaldatenTyp?typ=${filterData.typ}`;
                }

            // Redirect to the constructed URL
            if (url) {
               window.location.href = url;
            }


            // Close modal after search
            const modalElement = document.getElementById('berichteModal');
            const modal = bootstrap.Modal.getInstance(modalElement);
            modal.hide();
        }