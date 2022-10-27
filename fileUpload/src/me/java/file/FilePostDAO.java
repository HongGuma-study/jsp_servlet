package me.java.file;

import me.java.database.JDBCMgr;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class FilePostDAO {

    private static FilePostDAO filePostDAO = null;

    private Connection conn = null;
    private PreparedStatement stmt = null;
    private ResultSet rs = null;

    private static final String FILEPOST_SELECT_ALL = "select * from filePost";
    private static final String FILEPOST_SELECT = "select * from filePost where uId = ?";
    private static final String FILEPOST_INSERT = "insert into filePost(uId, title, fileInfo) values(?, ?, ?)";
    private static final String FILEPOST_DELETE = "delete filePost where uId = ?";

    private FilePostDAO() {}

    public static FilePostDAO getInstance() {
        if (filePostDAO == null) {
            filePostDAO = new FilePostDAO();
        }
        return filePostDAO;
    }


    public FilePost select(String uId) {
        FilePost filePost = null;
        List<FileInfo> fileIntoList;

        try {
            conn = JDBCMgr.getConnection();
            stmt = conn.prepareStatement(FILEPOST_SELECT);
            stmt.setString(1, uId);

            rs = stmt.executeQuery();

            if (rs.next()) {
                String mId = rs.getString("uId");
                String title = rs.getString("title");
                String fileInfo = rs.getString("fileInfo");
                fileIntoList = parseStringTOFileInfoList(fileInfo);
                filePost = new FilePost(mId, title, fileIntoList);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCMgr.close(rs, stmt, conn);
        }
        return filePost;
    }

    public List<FilePost> selectAll() {
        List<FilePost> filePostList = new LinkedList<>();
        FilePost filePost = null;
        try {
            conn = JDBCMgr.getConnection();
            stmt = conn.prepareStatement(FILEPOST_SELECT_ALL);

            rs = stmt.executeQuery();
            while (rs.next()) {
                String mId = rs.getString("uId");
                String title = rs.getString("title");
                String fileInfo = rs.getString("fileInfo");
                List<FileInfo> fileInfoList = parseStringTOFileInfoList(fileInfo);

                filePost = new FilePost(mId, title, fileInfoList);
                filePostList.add(filePost);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCMgr.close(rs, stmt, conn);
        }
        return filePostList;
    }

    public int insert(FilePost filePost) {
        int res = 0;
        try {
            conn = JDBCMgr.getConnection();
            stmt = conn.prepareStatement(FILEPOST_INSERT);
            stmt.setString(1, filePost.getUserId());
            stmt.setString(2, filePost.getTitle());
            stmt.setString(3, filePost.getFiles().toString());
            res = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCMgr.close(rs, stmt, conn);
        }
        return res;
    }

    public int delete(String uId) {
        int res = 0;
        try {
            conn = JDBCMgr.getConnection();
            stmt = conn.prepareStatement(FILEPOST_DELETE);
            stmt.setString(1, uId);
            res = stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            JDBCMgr.close(rs, stmt, conn);
        }
        return res;
    }

    public List<FileInfo> parseStringTOFileInfoList(String string){
        LinkedList<FileInfo> fileInfoLinkedList = new LinkedList<>();
        List<String> tokens = new LinkedList<>();

        StringTokenizer stringTokenizer = new StringTokenizer(string,"\'");
        int cnt = 0;
        while(stringTokenizer.hasMoreTokens()){
            cnt++;
            String str = stringTokenizer.nextToken();
            if(cnt % 2 != 0) continue;

            tokens.add(str);
            FileInfo fileInfo = null;
            if(tokens.size() == 4){
                fileInfo = new FileInfo();
                fileInfo.setFileName(tokens.get(0));
                fileInfo.setChangedFileName(tokens.get(1));
                fileInfo.setFileType(tokens.get(2));
                fileInfo.setFileLocation(tokens.get(3));

                tokens.clear();
                fileInfoLinkedList.add(fileInfo);
            }
        }

        return fileInfoLinkedList;
    }
}
