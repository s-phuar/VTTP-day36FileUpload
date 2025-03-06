import { Component, ElementRef, inject, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { FileUpload } from '../fileupload.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-upload',
  standalone: false,
  templateUrl: './upload.component.html',
  styleUrl: './upload.component.css'
})
export class UploadComponent implements OnInit{

  //allows access to the file input element in the html
  @ViewChild('fileUpload')
  imageFile!: ElementRef

  private fb = inject(FormBuilder)
  private fileUploadSvc = inject(FileUpload)
  private router = inject(Router)
  form !: FormGroup

  dataUri !: string | null //dataUri could be string or null
  blob !: Blob

  ngOnInit(): void {
    this.createForm()
  }


  // this.imageFile is an ElementRef, which gives access to the underlying native <input> element.
  // this.imageFile.nativeElement refers to the actual DOM element (<input type="file">).
  // .files is a FileList, which is an array-like object containing selected files.
  // .files[0] is the first selected file, which is a File object.
  upload(){
    console.info('uploading...')
    console.info('ElementRef:', this.imageFile) //ElementRef {nativeElement: input}
    console.info('Native Element', this.imageFile.nativeElement) //<input type="file" accept="imageasdasdas/whocares">
    console.info('Files List:', this.imageFile.nativeElement.files)
    const file = this.imageFile.nativeElement.files[0]; // A File selected by the user
    console.log("filename:", file.name); // Outputs: "House.png"
    console.log("filesize:", file.size); // Outputs: 117793 (size in bytes)
    console.log("filetype:", file.type); // Outputs: "image/jpeg"

    this.fileUploadSvc.upload(this.form.value, file) //file type is a subclass of blob
        .then((result =>{ //result is postId json and image string from spring controller
        console.log(result)
        this.router.navigate(['/image', result.postId]) //path variable routing only sends postId, see image component for get image
      }))
  }



  // //on png image selection, read it as base64 string
  // onFileChange(event: any){
  //   //event.target refers to the 'input' element
  //   const input = event.target as HTMLInputElement 
  //   if(input.files && input.files.length > 0){
  //     //need to extract image attached to input element file type
  //     const file = input.files[0]
  //     console.log(file)
  //     //use filereader to convert file to base64 string
  //     //store the string in this.dataUri
  //     const reader = new FileReader()
  //     reader.onload = () => {
  //       this.dataUri = reader.result as string //store base64 image string in this.datauri
  //     }
  //     reader.readAsDataURL(file)
  //     console.log(this.dataUri)
  //   }
  // }
  // //turn datauri into blob
  // dataUriToBlob(dataUri: string): Blob{
  //   const [meta, base64Data] = dataUri.split(',') //splitting the front bit "data:image/png;base64" from the rest
  //   const mimeMatch = meta.match(/:(.*?);/) //extract MIME type

  //   const mimeType = mimeMatch ? mimeMatch[1] : 'application/octet-stream' //indicate its a binary
  //   const byteString = atob(base64Data) //decode base64 string into binary
  //   //construct array buffer
  //   const ab = new ArrayBuffer(byteString.length)
  //   const ia = new Uint8Array(ab)
  //   for(let i = 0; i < byteString.length; i++){
  //     ia[i] = byteString.charCodeAt(i)
  //   }
  //   //create blob from binary data, converts base64 string into an actual file
  //   return new Blob([ia], {type: mimeType})
  // }

  // upload(){
  //   console.info('>>>uploading an image....')
  //   console.log(this.dataUri)
  //   // stop if dataUri is empty(falsy) "no image selected", ! makes it truthy
  //   if(!this.dataUri){
  //     return
  //   }
  //   //logs the base64 string
  //   this.blob = this.dataUriToBlob(this.dataUri)//convert uri to blob
  //   const formValue = this.form.value //grab the formgroup with 'comment' form control
  //   this.fileUploadSvc.upload(formValue, this.blob)
  //     .then((result =>{ //result is postId json and image string from spring controller
  //       console.log(result)
  //       this.router.navigate(['/image', result.postId]) //path variable routing only sends postId, see image component for get image
  //     }))
  // }

  // mandatory comment, no disable button for the below though
  createForm(){
    this.form = this.fb.group({
      comments: this.fb.control<string>('', [Validators.required, Validators.minLength(1)])
    })
  }



}

