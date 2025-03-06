import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { lastValueFrom } from "rxjs";
import { UploadResult } from "./model";

@Injectable()

export class FileUpload{
    private http = inject(HttpClient)

    // upload image to sql via multipart form
    //returns a 
    //note that our form did not use enctype="multipart/form-data" as we set the formData below
    upload(form:any, image:Blob){
        const formData = new FormData()
        formData.set('comments', form['comments']) //pulls out form control 'comment' from form group
        formData.set('file', image)

        //sending over form control and blob
        return lastValueFrom(this.http.post<UploadResult>('/api/uploadbucket', formData))

    }

    // get image from sql, almost immediately after upload
    getImage(postId: string){
        return lastValueFrom(this.http.get<UploadResult>(`/api/get-imagebucket/${postId}`))
    }
    



}