package dev.invoice.authoring.models;

public class ApplicationResponse<T> {
    public ApplicationResponse(Object response){
        Response = (T) response;
        Meta = null;
        Include = null;
        Error = null;
    }
    public ApplicationResponse(Object response, Object meta){
        Response = (T) response;
        Meta = meta;
        Include = null;
        Error = null;
    }

    public ApplicationResponse(Object response, Object meta, Object include){
        Response = (T) response;
        Meta = meta;
        Include = include;
        Error = null;
    }

    public ApplicationResponse(Object response, Object meta, Object include, Object error){
        Response = (T) response;
        Meta = meta;
        Include = include;
        Error = error;
    }

    public final T Response;
    public final Object Meta;
    public final Object Include;
    public final Object Error;
}
