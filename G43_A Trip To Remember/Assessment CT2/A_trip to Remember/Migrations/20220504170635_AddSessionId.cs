using System;
using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ATripToRemember.Migrations
{
    public partial class AddSessionId : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "BookingDate",
                table: "HotelReservations");

            migrationBuilder.AddColumn<string>(
                name: "PaymentIntentId",
                table: "HotelReservations",
                type: "nvarchar(max)",
                nullable: true);

            migrationBuilder.AddColumn<string>(
                name: "SessionId",
                table: "HotelReservations",
                type: "nvarchar(max)",
                nullable: true);
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropColumn(
                name: "PaymentIntentId",
                table: "HotelReservations");

            migrationBuilder.DropColumn(
                name: "SessionId",
                table: "HotelReservations");

            migrationBuilder.AddColumn<DateTime>(
                name: "BookingDate",
                table: "HotelReservations",
                type: "datetime2",
                nullable: false,
                defaultValue: new DateTime(1, 1, 1, 0, 0, 0, 0, DateTimeKind.Unspecified));
        }
    }
}
