import { useEffect, useState } from "react";
import TimeInput from "@components/video/TimeInput";
import Argola from "@components/utils/Argola";
import { Anotation, AnotationPublicFields } from "@hooks/useVideoAndAnotations";

export default function AnotationOptions({
    maxTime,
    anotation,
    edit,
    isEditing,
    close
}: {
    maxTime: number,
    anotation: Anotation | undefined,
    edit: (anotationChange: AnotationPublicFields) => void,
    isEditing: boolean,
    close: () => void,
}) {
    const [ editedAnotation, setEditedAnotation ] = useState({ anotation: "", videoInstant: 0 });
    const [ argolas, setArgolas ] = useState<{y: number, amount: undefined[]}>({y: 0, amount: []});

    useEffect(() => {
        if (!isEditing)
            close();
    }, [isEditing]);

    useEffect(() => {
        setEditedAnotation({
            anotation: anotation === undefined ? "" : anotation.anotation,
            videoInstant: anotation === undefined ? 0 : anotation.videoInstant
        });
    }, [anotation]);

    useEffect(() => {
        function onResize() {
            const { clientWidth, clientHeight } = document.querySelector("#add-video-modal-parent-element div")!;

            setArgolas({
                amount: new Array(Math.floor(clientWidth/30)).fill(undefined),
                y: -clientHeight/2
            });
        }

        window.addEventListener("resize", onResize);

        onResize();

        return () => {
            window.removeEventListener("resize", onResize);
        }
    }, []);

    return (
        <div
            id="add-video-modal-parent-element"
            className={`absolute top-0 h-full w-full bg-black/50 flex items-center justify-center ${anotation !== undefined ? "opacity-100 z-1" : "opacity-0 -z-1"}`}
            onClick={e => {
                e.stopPropagation();

                if ((e.target as HTMLDivElement).id !== "add-video-modal-parent-element")
                    return;

                close();
            }}
        >
            <div className={`h-[58%] w-[65%] bg-[#ffffbd] flex flex-col items-stretch justify-center content-stretch gap-2.5 p-6 rounded-3xl absolute`}>
                {argolas.amount.map((_, i) =>
                    <Argola
                        key={i}
                        radio={40}
                        x={i*29}
                        y={argolas.y}
                    />
                )}
                <header className="text-2xl font-bold text-center">
                    Editar anotação
                </header>
                <div className="flex flex-col">
                    <label
                        htmlFor="anotation"
                        className="border-b border-red-600 cursor-text"
                    >
                        Anotação
                    </label>
                    <textarea
                        id="anotation"
                        placeholder=" "
                        value={editedAnotation.anotation}
                        onChange={e => setEditedAnotation(cur => ({...cur, anotation: e.target.value}))}
                        className="underline underline-offset-4 decoration-red-600 placeholder-shown:border-b border-red-600 resize-none"
                    />
                </div>
                <div className="flex flex-col gap-2">
                    <label
                        className="border-b border-red-600"
                        htmlFor="anotacao"
                    >
                        Momento
                    </label>
                    <TimeInput
                        initialTime={editedAnotation.videoInstant}
                        maxTime={maxTime}
                        setTime={n => setEditedAnotation(cur => ({...cur, videoInstant: n}))}
                    />
                </div>
                <div className="border-b border-red-600 py-1 flex gap-2">
                    <button
                        className={`${isEditing ? "bg-gray-400 cursor-wait" : "bg-green-400 cursor-pointer"} rounded-2xl px-2 py-0.5 outline-none`}
                        onClick={() => edit(editedAnotation)}
                    >
                        Editar
                    </button>
                    <button
                        className="bg-red-600 cursor-pointer rounded-2xl px-2 py-0.5 transition-colors transition-700 outline-none"
                        onClick={close}
                    >
                        Cancelar
                    </button>
                </div>
            </div>
        </div>
    );
}