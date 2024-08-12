package com.example.configuration;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.geotools.api.referencing.crs.CoordinateReferenceSystem;
import org.geotools.api.referencing.operation.MathTransform;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.example") // 扫描指定包下的组件
public class Config {

    @Bean
    public CoordinateReferenceSystem geographicCRS() throws Exception {
        return DefaultGeographicCRS.WGS84;
    }

    @Bean
    public CoordinateReferenceSystem projectedCRS() throws Exception {
        return CRS.decode("EPSG:3857");
    }

    @Bean
    public MathTransform transformToProjected(CoordinateReferenceSystem geographicCRS, CoordinateReferenceSystem projectedCRS) throws Exception {
        return CRS.findMathTransform(geographicCRS, projectedCRS);
    }

    @Bean
    public MathTransform transformToGeographic(CoordinateReferenceSystem projectedCRS, CoordinateReferenceSystem geographicCRS) throws Exception {
        return CRS.findMathTransform(projectedCRS, geographicCRS);
    }
}
