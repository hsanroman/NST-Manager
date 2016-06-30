## 3rd Party Custom Commands

### Nest Thermostat

__Command:__ | __Values:__ | __Description__ |
:---: | :---: | :---: |
refresh() | none | Requests current data from parent |
poll() | none | Requests current data from parent |
away() | none | Sets the Nest Location Away |
present()  | none | Sets the Nest Location Home |
setPresence() | (string) "present", "away" | Set presennce based on steing value |
changeMode() | none | Cycles to next available mode |
Nest Weather Device | *v2.5.0* |

        command "setThermostatMode"
        command "levelUpDown"
        command "levelUp"
        command "levelDown"
        command "heatingSetpointUp"
        command "heatingSetpointDown"
        command "coolingSetpointUp"
        command "coolingSetpointDown"