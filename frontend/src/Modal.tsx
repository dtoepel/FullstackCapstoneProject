import React, { useEffect, useRef } from "react";

type ModalProps = {
    open: boolean;
    title?: string;
    onClose: () => void;
    children: React.ReactNode;
};

export default function Modal({title, onClose, children}: Readonly<ModalProps>) {
    const dlgRef = useRef<HTMLDialogElement>(null);
    const titleId = useRef(
        `modal-title-${crypto.getRandomValues(new Uint32Array(1))[0].toString(36)}`
    );

    useEffect(() => {
        const dlg = dlgRef.current;

        if (!dlg) return;

        if (!dlg.open) dlg.showModal();

        const onCancel = (e: Event) => { e.preventDefault(); onClose(); };
        dlg.addEventListener("cancel", onCancel);
        return () => dlg.removeEventListener("cancel", onCancel);

    }, [onClose]);

    return (
        <dialog
            ref={dlgRef}
            className="modal"
            aria-labelledby={title ? titleId.current : undefined}
        >
            <header className="modal-header">
                {<h3 style={{margin:"0"}} id={titleId.current}>{title}</h3>}
                <button className="modal-close" type="button" onClick={onClose} aria-label="Close">
                    ✖
                </button>
            </header>
            <div className="modal-content">{children}</div>
        </dialog>
    );
}