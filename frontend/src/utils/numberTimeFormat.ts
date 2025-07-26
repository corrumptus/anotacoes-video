export function numberToTime(nInSeconds: number, forceHour: boolean = false): string {
    const seconds = nInSeconds % 60;
    const minutes = ((nInSeconds - seconds)/60) % 60;

    if (!forceHour && nInSeconds < 3600)
        return `${timeString(minutes)}:${timeString(seconds)}`;

    const hours = (nInSeconds - minutes*60 - seconds) / 3600;

    return `${timeString(hours)}:${timeString(minutes)}:${timeString(seconds)}`;
}

export function objectToTime(o: { hours: number, minutes: number, seconds: number }): string {
    return Object.values(o).join(":");
}

export function timeToNumber(time: string): number {
    const units = time.split(":").reverse();

    return units.reduce((acc, cur, i) => acc + (+cur * 60**i), 0);
}

export function timeObjectToNumber(o: { hours: string, minutes: string, seconds: string }): number {
    return Number(o.hours)*3600 + Number(o.minutes)*60 + Number(o.seconds);
}

export function timeString(n: number) {
    return String(Math.round(n)).padStart(2, "0");
}