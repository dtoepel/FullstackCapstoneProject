import React, { useEffect, useRef } from "react";

type ModalProps = {
    open: boolean;
    title?: string;
    onClose: () => void;
    children: React.ReactNode;
};

export default function Modal({ open, title, onClose, children }: Readonly<ModalProps>) {
    const dlgRef = useRef<HTMLDialogElement>(null);
    const titleId = useRef(
        `modal-title-${crypto.getRandomValues(new Uint32Array(1))[0].toString(36)}`
    );

    useEffect(() => {
        const dlg = dlgRef.current;
        if (!dlg) return;

        if (open) {
            if (!dlg.open) dlg.showModal();
            const onCancel = (e: Event) => { e.preventDefault(); onClose(); };
            dlg.addEventListener("cancel", onCancel);
            return () => dlg.removeEventListener("cancel", onCancel);
        } else if (dlg.open) {
            dlg.close();
        }
    }, [open, onClose]);

    if (!open) return null;

    const onBackdropClick: React.MouseEventHandler<HTMLDialogElement> = (e) => {
        const rect = e.currentTarget.getBoundingClientRect();
        const inside =
            e.clientX >= rect.left && e.clientX <= rect.right &&
            e.clientY >= rect.top && e.clientY <= rect.bottom;
        if (!inside) onClose();
    };

    return (
        <dialog
            ref={dlgRef}
            className="modal"
            aria-labelledby={title ? titleId.current : undefined}
            onMouseDown={onBackdropClick}
        >
            <header className="modal-header">
                {title && <h3 id={titleId.current}>{title}</h3>}
                <button className="modal-close modal-close--icon" type="button" onClick={onClose} aria-label="Close">
                    ×
                </button>
            </header>
            <div className="modal-content">{children}</div>
        </dialog>
    );
}