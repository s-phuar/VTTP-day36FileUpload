import { Component, inject, OnInit } from '@angular/core';
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

  private fb = inject(FormBuilder)
  private fileUploadSvc = inject(FileUpload)
  private router = inject(Router)
  form !: FormGroup

  dataUri !: string | null //dataUri could be string or null
  blob !: Blob

  ngOnInit(): void {
    this.createForm()
  }

  //on png image selection, read it as base64 string
  onFileChange(event: any){
    //event.target refers to the 'input' element
    const input = event.target as HTMLInputElement 
    if(input.files && input.files.length > 0){
      //need to extract image attached to input element file type
      const file = input.files[0]
      console.log(file)
      //use filereader to convert file to base64 string
      //store the string in this.dataUri
      const reader = new FileReader()
      reader.onload = () => {
        this.dataUri = reader.result as string //store base64 image string in this.datauri
      }
      reader.readAsDataURL(file)
      console.log(this.dataUri)
    }
  }
  //turn datauri into blob
  dataUriToBlob(dataUri: string): Blob{
    const [meta, base64Data] = dataUri.split(',') //splitting the front bit "data:image/png;base64" from the rest
    const mimeMatch = meta.match(/:(.*?);/) //extract MIME type

    const mimeType = mimeMatch ? mimeMatch[1] : 'application/octet-stream' //indicate its a binary
    const byteString = atob(base64Data) //decode base64 string into binary
    //construct array buffer
    const ab = new ArrayBuffer(byteString.length)
    const ia = new Uint8Array(ab)
    for(let i = 0; i < byteString.length; i++){
      ia[i] = byteString.charCodeAt(i)
    }
    //create blob from binary data, converts base64 string into an actual file
    return new Blob([ia], {type: mimeType})
  }

  upload(){
    console.info('>>>uploading an image....')
    console.log(this.dataUri)
    // stop if dataUri is empty(falsy) "no image selected", ! makes it truthy
    if(!this.dataUri){
      return
    }
    //logs the base64 string
    this.blob = this.dataUriToBlob(this.dataUri)//convert uri to blob
    const formValue = this.form.value //grab the formgroup with 'comment' form control
    this.fileUploadSvc.upload(formValue, this.blob)
      .then((result =>{ //result is postId json and image string from spring controller
        console.log(result)
        this.router.navigate(['/image', result.postId]) //path variable routing only sends postId, see image component for get image
      }))
  }

  // mandatory comment, no disable button for the below though
  createForm(){
    this.form = this.fb.group({
      comments: this.fb.control<string>('', [Validators.required, Validators.minLength(1)])
    })
  }



}

