import {useNavigate} from "react-router-dom";
import type {MyError} from "./ElectionData.ts";

export type ErrorMessageProps = {
    error:MyError;
}

export default function ErrorMessage(props:Readonly<ErrorMessageProps>) {
    const nav = useNavigate();
    return(
        <>
            <p className={"error-message"}>
                Error {props.error.status}:<br/>
                {props.error.message2?props.error.message2:props.error.message}
            </p>
            <br/>
            <button onClick={() => nav(-1)}>Back</button>
        </>
    )
}