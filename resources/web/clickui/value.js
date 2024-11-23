class Value {
    constructor(element, parentMod, index) {
        this.element = element;
        this.parent = parentMod;
        this.index = index;
        this.enabled = false;
        this.key = 0;
    }

    convert(v) {
        return v.substring(0, v.length - 2);
    }
}