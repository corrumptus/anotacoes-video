import { useRef, useState, useEffect } from "react";
import { numberToTime, timeString, timeObjectToNumber } from "@utils/numberTimeFormat";

export default function TimeInput({
    initialTime,
    maxTime,
    setTime
}: {
    initialTime: number,
    maxTime: number,
    setTime: (n: number) => void
}) {
    const timeInputRef = useRef<HTMLDivElement>(null);
    const [ timeInput, setTimeInput ] = useState<{
        hours: string,
        minutes: string,
        seconds: string
    }>({ hours: "00", minutes: "00", seconds: "00" });
    const [ inputIsVisible, setInputIsVisible ] = useState(false);

    useEffect(() => {
        const timeSplit = numberToTime(initialTime, true).split(":");
        setTimeInput({
            hours: timeSplit[0],
            minutes: timeSplit[1],
            seconds: timeSplit[2]
        });
    }, [initialTime]);

    useEffect(() => {
        function onClick(e: MouseEvent) {
            if (inputIsVisible && !timeInputRef.current!.contains(e.target as HTMLElement))
                switchWheelVisibility();
        }

        document.addEventListener("click", onClick);

        return () => {
            document.removeEventListener("click", onClick);
        }
    }, [inputIsVisible]);

    useEffect(() => {
        setTime(timeObjectToNumber(timeInput));
    }, [timeInput]);

    function switchWheelVisibility() {
        setInputIsVisible(cur => !cur);
    }

    function select(id: string, value: number) {
        return () => {
            setTimeInput(cur => {
                const newTime = {...cur, [id]: timeString(value)};

                if (timeObjectToNumber(newTime) > maxTime)
                    return cur;

                return newTime;
            });
        };
    }

    return (
        <div
            className="border-b border-red-600 relative"
            ref={timeInputRef}
        >
            <div
                className="w-fit flex gap-1 cursor-pointer"
                onClick={switchWheelVisibility}
            >
                <div id="hours">
                    {timeInput.hours}
                </div>
                <span>:</span>
                <div id="minutes">
                    {timeInput.minutes}
                </div>
                <span>:</span>
                <div id="seconds">
                    {timeInput.seconds}
                </div>
            </div>
            <div className={`h-[100px] absolute top-[22px] ${inputIsVisible ? "opacity-100" : "opacity-0 hidden"} flex gap-2 p-2 bg-white rounded-2xl cursor-pointer`}>
                <div className="w-[18px] h-full overflow-hidden">
                    <div className="w-full h-[99%] overflow-x-hidden overflow-y-auto pr-[33px] flex flex-col">
                        {new Array(Math.min(Math.floor(maxTime/3600)+1, 60)).fill(undefined).map((_, i) => i)
                            .map(t =>
                                <div key={t} onClick={select("hours", t)}>
                                    {timeString(t)}
                                </div>
                            )
                        }
                    </div>
                </div>
                <div className="w-[18px] h-full overflow-hidden">
                    <div className="w-full h-[99%] overflow-x-hidden overflow-y-auto pr-[33px] flex flex-col">
                        {new Array(Math.min(Math.floor(maxTime/60)+1, 60)).fill(undefined).map((_, i) => i)
                            .map(t =>
                                <div key={t} onClick={select("minutes", t)}>
                                    {timeString(t)}
                                </div>
                            )
                        }
                    </div>
                </div>
                <div className="w-[18px] h-full overflow-hidden">
                    <div className="w-full h-[99%] overflow-x-hidden overflow-y-auto pr-[33px] flex flex-col">
                        {new Array(Math.min(maxTime+1, 60)).fill(undefined).map((_, i) => i)
                            .map(t =>
                                <div key={t} onClick={select("seconds", t)}>
                                    {timeString(t)}
                                </div>
                            )
                        }
                    </div>
                </div>
            </div>
        </div>
    );
}