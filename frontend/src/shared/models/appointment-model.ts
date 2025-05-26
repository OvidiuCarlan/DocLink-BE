export interface AppointmentData {
    id?: number;
    userId: number;
    // postId: number;
    date: string;
    time: string;
    notes: string;
    procedureTitle?: string;
}

export interface GetAppointmentsResponse {
    appointments: AppointmentData[];
}