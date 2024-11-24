class Module {
    constructor(element, parentCat, index, name) {
        this.element = element;
        this.parent = parentCat;
        this.index = index;
        this.enabled = false;
        this.key = 0;
        this.element.addEventListener("mouseup", this.expand.bind(this));
        this.expanded = false;
        this.element.style.height = this.expanded ? "" : "40px";
        window.mcefQuery({
            request: `clickui/values:${name}`,
            onSuccess: function (msg) {
                const array = JSON.parse(msg);
                this.enabled = Boolean(array[0]["bool:enabled"]);
                this.key = Number(array[0]["number:key"]);
                for (let i = 1; i < array.length; i++) {
                    const obj = array[i];
                    const entries = Object.entries(obj);
                    for (let j = 0; j < entries.length; j++) {
                        const entry = entries[j]; //"number:min, 0"
                        const key = entry[0];
                        const value = entry[1];
                        const type_name = key.split(":");
                        const type = type_name[0];
                        const name = type_name[1];
                        switch (type) {
                            case "number":
                                let min = value["min"];
                                let max = value["max"];
                                let inc = value["inc"];
                                let val = value["value"];
                                break;
                            case "mode":
                                let modes = value["modes"];
                                let current = value["value"];
                                break;
                            default:
                                const valueDiv = document.createElement("div");
                                valueDiv.classList.add("value");
                                valueDiv.innerHTML = `${name}: ${value}`;
                                element.appendChild(valueDiv);
                                new Value(valueDiv, element, j);
                        }
                    }
                }
            },
            onFailure: function (err, msg) {
                console.log(msg);
            },
        });
    }

    expand(event) {
        if (event.button === 1) {
            this.expanded = !this.expanded;
            this.element.style.height = this.expanded ? "" : "40px";
        }
    }

    convert(v) {
        return v.substring(0, v.length - 2);
    }
}
