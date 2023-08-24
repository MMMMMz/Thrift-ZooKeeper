struct Student{
    1:optional string name
    2:optional i32 age
    3:optional string sex
    4:optional string address
}
exception DataException{
    1:optional string code
    2:optional string message
    3:optional string dateTime
}
service StudentService{
    Student getStudentByName(1:required string name) throws (1:DataException dataException)

    void save(1:required Student student) throws (1:DataException dataException)
}