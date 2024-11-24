class Panel {
    constructor(element) {
        this.element = element;
        this.element.style.position = "absolute";
        this.element.addEventListener("mousedown", this.handleDragStart.bind(this));
        document.addEventListener("mousemove", this.handleDrag.bind(this));
        document.addEventListener("mouseup", this.handleDragEnd.bind(this));
        let val = element.id;
        window.mcefQuery({
            request: `clickui/mods:${val}`,
            persistent: false,
            onSuccess: function (msg) {
                const array = JSON.parse(msg);
                for (let i = 0; i < array.length; i++) {
                    const val = array[i];
                    const moduleDiv = document.createElement("div");
                    moduleDiv.classList.add("module");
                    moduleDiv.classList.add("content");
                    const nameDiv = document.createElement("div");
                    nameDiv.innerHTML = `${val}`;
                    nameDiv.classList.add("moduleName");
                    moduleDiv.appendChild(nameDiv);
                    element.appendChild(moduleDiv);
                    new Module(moduleDiv, element, i, val);
                }
            },
            onFailure: function (err, msg) {
                console.log(msg);
            },
        });
    }

    convert(v) {
        return v.substring(0, v.length - 2);
    }

    setMaxZIndex() {
        let maxZIndex = 0;
        const elements = document.querySelectorAll("*");
        elements.forEach(function (el) {
            const zIndex = parseInt(window.getComputedStyle(el).zIndex, 10);
            if (zIndex > maxZIndex) maxZIndex = zIndex;
        });
        this.element.style.zIndex = String(maxZIndex + 1);
    }

    handleDragStart(event) {
        event.preventDefault();
        const style = this.element.style;
        this.startX = event.clientX - this.convert(style.left);
        this.startY = event.clientY - this.convert(style.top);
        this.setMaxZIndex();
        this.dragging = this.startY <= 35;
    }

    handleDrag(event) {
        event.preventDefault();
        if (!this.dragging) return;
        const style = this.element.style;
        style.left = `${event.clientX - this.startX}px`;
        style.top = `${event.clientY - this.startY}px`;
    }

    handleDragEnd(_) {
        this.dragging = false;
    }
}
