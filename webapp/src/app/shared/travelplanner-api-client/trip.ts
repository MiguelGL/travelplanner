export interface Trip {
  id: number;
  updated: Date;

  startDate: Date;
  endDate: Date;

  destination: string;

  daysToStart: number;

  comment: string;
}
