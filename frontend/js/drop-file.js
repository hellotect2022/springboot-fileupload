const FileDragDropUI = {
    dropArea: document.getElementById('drop-area'),
    uploadFiles: [],

    init() {
        console.log('FileUploader initialized',this);
        ;['dragenter', 'dragover', 'dragleave', 'drop'].forEach(eventName => {
            this.dropArea.addEventListener(eventName, this.preventDefaults, false);
            document.body.addEventListener(eventName, this.preventDefaults, false);
        });

        this.dropArea.addEventListener('drop', this.handleDrop.bind(this), false);
    },

    preventDefaults (e) {
        e.preventDefault();
        e.stopPropagation();
    },

    handleDrop(e) {
        let dt = e.dataTransfer;
        let files = dt.files;
        ([...files]).forEach(a=>this.uploadFile2(a));
    },

    uploadFile2(file) {
        this.uploadFiles.push(file);
        let div = document.createElement('div');
        div.textContent = file.name;
        this.dropArea.appendChild(div);
    },

    initialize() {
        this.uploadFiles = [];
        let divs = document.querySelectorAll('#drop-area div');
        divs.forEach(div => div.parentNode.removeChild(div));
    }
}