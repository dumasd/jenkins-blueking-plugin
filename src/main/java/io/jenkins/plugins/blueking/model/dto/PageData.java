package io.jenkins.plugins.blueking.model.dto;

import com.alibaba.fastjson2.JSON;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import javax.servlet.ServletException;
import lombok.Getter;
import lombok.Setter;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Flavor;

@Setter
@Getter
public class PageData<T extends Serializable> implements Serializable, HttpResponse {

    private static final long serialVersionUID = -6838320397447907213L;

    private Integer count;

    private List<T> info;

    @Override
    public void generateResponse(StaplerRequest req, StaplerResponse rsp, Object node)
            throws IOException, ServletException {
        byte[] bytes = JSON.toJSONBytes(this);
        rsp.setContentType(Flavor.JSON.contentType);
        try (OutputStream os = rsp.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
    }
}
