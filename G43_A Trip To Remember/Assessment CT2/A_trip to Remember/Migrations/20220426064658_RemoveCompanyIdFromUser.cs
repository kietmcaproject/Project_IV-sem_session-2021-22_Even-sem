using Microsoft.EntityFrameworkCore.Migrations;

#nullable disable

namespace ATripToRemember.Migrations
{
    public partial class RemoveCompanyIdFromUser : Migration
    {
        protected override void Up(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.DropForeignKey(
                name: "FK_AspNetUsers_AirLines_AirlineId",
                table: "AspNetUsers");

            migrationBuilder.DropIndex(
                name: "IX_AspNetUsers_AirlineId",
                table: "AspNetUsers");

            migrationBuilder.DropColumn(
                name: "AirlineId",
                table: "AspNetUsers");
        }

        protected override void Down(MigrationBuilder migrationBuilder)
        {
            migrationBuilder.AddColumn<int>(
                name: "AirlineId",
                table: "AspNetUsers",
                type: "int",
                nullable: true);

            migrationBuilder.CreateIndex(
                name: "IX_AspNetUsers_AirlineId",
                table: "AspNetUsers",
                column: "AirlineId");

            migrationBuilder.AddForeignKey(
                name: "FK_AspNetUsers_AirLines_AirlineId",
                table: "AspNetUsers",
                column: "AirlineId",
                principalTable: "AirLines",
                principalColumn: "Id",
                onDelete: ReferentialAction.Cascade);
        }
    }
}
